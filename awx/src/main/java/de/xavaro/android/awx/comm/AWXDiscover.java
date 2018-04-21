package de.xavaro.android.awx.comm;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.ParcelUuid;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.xavaro.android.awx.base.AWX;
import de.xavaro.android.awx.simple.Json;
import de.xavaro.android.awx.simple.Simple;

public class AWXDiscover
{
    private static final String LOGTAG = AWXDiscover.class.getSimpleName();

    public final static int MANUFACTURER_AWOX = 352;

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
    private AWXGattCallback callback;

    private final Object mutex = new Object();
    private final ArrayList<JSONObject> scanResults = new ArrayList<>();
    private final ArrayList<String> connecting = new ArrayList<>();

    public AWXDiscover(Context context)
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

                synchronized (connecting)
                {
                    if (connecting.contains(macaddr))
                    {
                        continue;
                    }

                    connecting.add(macaddr);
                }

                Log.d(LOGTAG, "discoverRunnable:"
                        + " macaddr=" + Json.getString(scanResult, "macaddr")
                        + " service=" + Json.getString(scanResult, "service")
                );

                if (callback == null) callback = new AWXGattCallback();

                BluetoothDevice device = adapter.getRemoteDevice(macaddr);
                BluetoothGatt gatt = device.connectGatt(context, false, callback);
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

        int vendor = 0;

        SparseArray<byte[]> manufacturerData = result.getScanRecord().getManufacturerSpecificData();

        if (manufacturerData.size() > 0)
        {
            for (int inx = 0; inx < manufacturerData.size(); inx++)
            {
                //
                // Abuse certain devices as beacons.
                //

                vendor = manufacturerData.keyAt(inx);
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

        JSONObject scanResult = new JSONObject();

        Json.put(scanResult, "macaddr", result.getDevice().getAddress());
        Json.put(scanResult, "service", serviceUuid.toString());

        synchronized (scanResults)
        {
            scanResults.add(scanResult);
        };

        Log.d(LOGTAG, "evaluateScan: ALT"
                + " addr=" + result.getDevice().getAddress()
                + " vendor=" + vendor
                + " name=" + result.getDevice().getName()
                + " service=" + serviceUuid.toString()
                + " scan=" + result.getScanRecord()
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static class AWXGattCallback extends BluetoothGattCallback
    {
        private static final String LOGTAG = AWXGattCallback.class.getSimpleName();

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState)
        {
            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                Log.d(LOGTAG, "onConnectionStateChange: connected.");

                gatt.discoverServices();
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                Log.d(LOGTAG, "onConnectionStateChange: disconnected.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status)
        {
            Log.d(LOGTAG, "onServicesDiscovered: status=" + status);

            List<BluetoothGattService> services = gatt.getServices();

            for (BluetoothGattService service : services)
            {
                Log.d(LOGTAG, "onServicesDiscovered: service=" + service.getUuid());

                List<BluetoothGattCharacteristic> charas = service.getCharacteristics();

                for (BluetoothGattCharacteristic chara : charas)
                {
                    Log.d(LOGTAG, "onServicesDiscovered: characteristic=" + chara.getUuid());
                }
            }
        }
    }
}
