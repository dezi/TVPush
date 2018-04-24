package de.xavaro.android.awx.comm;

import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.util.SparseArray;
import android.content.Context;
import android.os.ParcelUuid;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.xavaro.android.awx.base.AWX;
import de.xavaro.android.awx.simple.Json;
import de.xavaro.android.awx.simple.Simple;

public class AWXDiscover
{
    private static final String LOGTAG = AWXDiscover.class.getSimpleName();

    private final static int MANUFACTURER_AWOX = 352;

    public static void startService(Context appcontext)
    {
        if ((AWX.instance != null) && (AWX.instance.discover == null))
        {
            AWX.instance.discover = new AWXDiscover(appcontext);
            AWX.instance.discover.startThread();
        }
    }

    public static void stopService()
    {
        if ((AWX.instance != null) && (AWX.instance.discover != null))
        {
            AWX.instance.discover.stopThread();
            AWX.instance.discover = null;
        }
    }

    private Context context;
    private BluetoothAdapter adapter;
    private Thread discoverThread;
    private BluetoothLeScanner scanner;

    private final Object mutex = new Object();
    private final ArrayList<JSONObject> scanResults = new ArrayList<>();

    private AWXDiscover(Context context)
    {
        this.context = context;
        this.adapter = Simple.getBTAdapter();
    }

    private void startThread()
    {
        synchronized (mutex)
        {
            if (discoverThread == null)
            {
                discoverThread = new Thread(discoverRunnable);
                discoverThread.start();
            }
        }
    }

    private void stopThread()
    {
        synchronized (mutex)
        {
            if (discoverThread != null)
            {
                discoverThread.interrupt();
                discoverThread = null;
            }
        }
    }

    private final Runnable discoverRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            {
                return;
            }

            startLEScanner();

            long exittime = System.currentTimeMillis() + 120 * 1000;
            ArrayList<String> connecting = new ArrayList<>();

            while ((discoverThread != null) && (System.currentTimeMillis() < exittime))
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception ignore)
                {
                }

                JSONObject scanResult = null;

                synchronized (scanResults)
                {
                    if (scanResults.size() > 0)
                    {
                        scanResult = scanResults.remove(0);
                    }
                }

                if (scanResult == null) continue;

                String macaddr = Json.getString(scanResult, "macaddr");

                if (connecting.contains(macaddr))
                {
                    continue;
                }

                connecting.add(macaddr);

                String meshname = Json.getString(scanResult, "meshname");
                short meshid = (short) Json.getInt(scanResult, "meshid");

                Log.d(LOGTAG, "discoverRunnable: macaddr=" + macaddr + " meshname=" + meshname + " meshid=" + meshid);

                if (meshname != null)
                {
                    AWXDevice awxdevice = new AWXDevice(context, meshid, meshname, macaddr);
                    awxdevice.connect();
                }
            }

            stopLEScanner();

            Log.d(LOGTAG, "discoverRunnable: done.");
        }
    };

    private void startLEScanner()
    {
        if ((scanner == null) && (adapter != null))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                scanner = adapter.getBluetoothLeScanner();
                scanner.startScan(scanCallback);

                Log.d(LOGTAG, "startLEScanner: scanner started.");
            }
        }
    }

    private void stopLEScanner()
    {
        if (scanner != null)
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                scanner.stopScan(scanCallback);
                scanner = null;

                Log.d(LOGTAG, "stopLEScanner: scanner stopped.");
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private final ScanCallback scanCallback = new ScanCallback()
    {
        @Override
        public void onScanResult(int callbackType, ScanResult result)
        {
            evaluateScan(callbackType, result);
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void evaluateScan(int callbackType, ScanResult result)
    {
        if (result.getScanRecord() == null) return;

        String name = result.getDevice().getName();

        int vendor = 0;
        byte[] manufdata = null;

        SparseArray<byte[]> manufacturerData = result.getScanRecord().getManufacturerSpecificData();

        if (manufacturerData.size() > 0)
        {
            for (int inx = 0; inx < manufacturerData.size(); inx++)
            {
                vendor = manufacturerData.keyAt(inx);
                manufdata = manufacturerData.get(vendor);
            }
        }

        if (vendor != MANUFACTURER_AWOX) return;

        ParcelUuid serviceUuid = null;
        List<ParcelUuid> serviceUuids = result.getScanRecord().getServiceUuids();

        if ((serviceUuids != null) && (serviceUuids.size() > 0))
        {
            for (ParcelUuid uuid : serviceUuids)
            {
                serviceUuid = uuid;
            }
        }

        if (serviceUuid == null) return;

        Log.d(LOGTAG, "evaluateScan: AWX"
                + " addr=" + result.getDevice().getAddress()
                + " vendor=" + vendor
                + " name=" + name
                //+ " service=" + serviceUuid.toString()
                //+ " scan=" + result.getScanRecord()
        );

        if (name == null) return;

        JSONObject scanResult = new JSONObject();

        Json.put(scanResult, "macaddr", result.getDevice().getAddress());
        Json.put(scanResult, "meshname", name);
        Json.put(scanResult, "meshid", getMeshId(manufdata));

        synchronized (scanResults)
        {
            scanResults.add(scanResult);
        };
    }

    private static short getMeshId(byte[] manufData)
    {
        if (manufData == null || manufData.length <= 14) return 0;

        return (short) (((manufData[3] & 0xff) << 8) + (manufData[2] & 255));
    }
}
