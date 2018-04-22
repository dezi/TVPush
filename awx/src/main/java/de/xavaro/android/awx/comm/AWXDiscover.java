package de.xavaro.android.awx.comm;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private final Map<BluetoothGatt, String> gatt2macaddr = new HashMap<>();
    private final Map<BluetoothGatt, ArrayList<BluetoothGattCharacteristic>> gatt2read = new HashMap<>();

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

                gatt2macaddr.put(gatt, macaddr);
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
    private class AWXGattCallback extends BluetoothGattCallback
    {
        private final String LOGTAG = AWXGattCallback.class.getSimpleName();

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
            if (status != 0) return;

            ArrayList<BluetoothGattCharacteristic> readme = new ArrayList<>();
            gatt2read.put(gatt,readme);

            List<BluetoothGattService> services = gatt.getServices();

            for (BluetoothGattService service : services)
            {
                Log.d(LOGTAG, "onServicesDiscovered: service=" + service.getUuid());

                List<BluetoothGattCharacteristic> charas = service.getCharacteristics();

                for (BluetoothGattCharacteristic chara : charas)
                {
                    Log.d(LOGTAG, "onServicesDiscovered: characteristic=" + chara.getUuid());

                    readme.add(chara);

                    List<BluetoothGattDescriptor> descs = chara.getDescriptors();

                    for (BluetoothGattDescriptor desc : descs)
                    {
                        Log.d(LOGTAG, "onServicesDiscovered: descriptor=" + desc.getUuid());
                    }
                }
            }

            readNext(gatt);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic chara,
                                         int status)
        {
            if (status != BluetoothGatt.GATT_SUCCESS)
            {
                Log.d(LOGTAG, " onCharacteristicRead status=" + status + " uuid=" + chara.getUuid());

                return;
            }

            byte[] data = chara.getValue();

            String tag = null;
            String val = null;

            switch(chara.getUuid().toString())
            {
                case AWXDefs.chara_device_name:
                    tag = "name";
                    val = chara.getStringValue(0).trim();
                    break;

                case AWXDefs.chara_appearance:
                    tag = "type";
                    val = getBytesToHexString(data, 0, data.length);
                    break;

                case AWXDefs.chara_model_number_string:
                    tag = "modnum";
                    val = chara.getStringValue(0).trim();
                    break;

                case AWXDefs.chara_serial_number_string:
                    tag = "serial";
                    val = chara.getStringValue(0).trim();
                    break;

                case AWXDefs.chara_firmware_revision_string:
                    tag = "firmware";
                    val = chara.getStringValue(0).trim();
                    break;

                case AWXDefs.chara_hardware_revision_string:
                    tag = "hardware";
                    val = chara.getStringValue(0).trim();
                    break;

                case AWXDefs.chara_manufacturer_name_string:
                    tag = "vendor";
                    val = chara.getStringValue(0).trim();
                    break;
            }

            Log.d(LOGTAG, " onCharacteristicRead tag=" + tag + " val=" + val);

            readNext(gatt);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void readNext(BluetoothGatt gatt)
    {
        ArrayList<BluetoothGattCharacteristic> readme = gatt2read.get(gatt);
        if ((readme == null) || (readme.size() == 0)) return;

        BluetoothGattCharacteristic chara = readme.remove(0);
        gatt.readCharacteristic(chara);
    }

    private static String getBytesToHexString(byte[] buffer, int start, int len)
    {
        String c;
        StringBuilder s = new StringBuilder();
        for (int i = start; i < start + len; i++)
        {
            c = Integer.toHexString(buffer[i] & 0xFF);
            s.append(c.length() < 2 ? "0" + c : c);
        }
        return s.toString();
    }
}
