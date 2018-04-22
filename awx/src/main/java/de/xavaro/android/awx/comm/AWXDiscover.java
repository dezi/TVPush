package de.xavaro.android.awx.comm;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.graphics.Color;
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

import com.telink.crypto.AES;

import org.json.JSONObject;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final Map<BluetoothGatt, ArrayList<AWXGattRequest>> gatt2execute = new HashMap<>();

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
            AES.initialize();

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

        meshId = getMeshId(manufdata);

        JSONObject scanResult = new JSONObject();

        Json.put(scanResult, "macaddr", result.getDevice().getAddress());
        Json.put(scanResult, "service", serviceUuid.toString());

        macaddr = result.getDevice().getAddress();

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

    private short meshId;
    private String macaddr;
    private byte[] mSessionKey;
    private byte[] mSessionRandom;
    private String mMeshNameStr = "pDmpKfcw";
    private String mMeshPasswordStr = "f6d292f5";

    private BluetoothGattCharacteristic cpair;
    private BluetoothGattCharacteristic ccommand;
    private BluetoothGattCharacteristic cstatus;
    private ArrayList<AWXGattRequest> executeme= new ArrayList<>();

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

            gatt2execute.put(gatt, executeme);

            List<BluetoothGattService> services = gatt.getServices();

            for (BluetoothGattService service : services)
            {
                Log.d(LOGTAG, "onServicesDiscovered: service=" + service.getUuid());

                List<BluetoothGattCharacteristic> charas = service.getCharacteristics();

                for (BluetoothGattCharacteristic chara : charas)
                {
                    Log.d(LOGTAG, "onServicesDiscovered: characteristic=" + chara.getUuid());

                    if (chara.getUuid().toString().equals(AWXDefs.CHARACTERISTIC_MESH_LIGHT_PAIR))
                    {
                        cpair = chara;
                    }
                    else
                    {
                        if (chara.getUuid().toString().equals(AWXDefs.CHARACTERISTIC_MESH_LIGHT_STATUS))
                        {
                            cstatus = chara;
                        }
                        else
                        {
                            if (chara.getUuid().toString().equals(AWXDefs.CHARACTERISTIC_MESH_LIGHT_COMMAND))
                            {
                                ccommand = chara;
                            }
                            else
                            {
                                executeme.add(new AWXGattRequest(gatt, chara, null, AWXGattRequest.MODE_READ_CHARACTERISTIC));
                            }
                        }
                    }

                    List<BluetoothGattDescriptor> descs = chara.getDescriptors();

                    for (BluetoothGattDescriptor desc : descs)
                    {
                        Log.d(LOGTAG, "onServicesDiscovered: descriptor=" + desc.getUuid());
                    }
                }
            }

            //
            // Pairing
            //

            mSessionRandom = new byte[8];
            new SecureRandom().nextBytes(mSessionRandom);

            byte[] meshName = Arrays.copyOf(mMeshNameStr.getBytes(), 16);
            byte[] meshPassword = Arrays.copyOf(mMeshPasswordStr.getBytes(), 16);

            byte[] data = AWXProtocol.getPairValue(meshName, meshPassword, mSessionRandom);
            executeme.add(new AWXGattRequest(gatt, cpair, data, AWXGattRequest.MODE_WRITE_CHARACTERISTIC));
            executeme.add(new AWXGattRequest(gatt, cpair, null, AWXGattRequest.MODE_READ_CHARACTERISTIC));

            //
            // Status
            //

            executeme.add(new AWXGattRequest(gatt, cstatus, new byte[]{(byte) 1}, AWXGattRequest.MODE_WRITE_CHARACTERISTIC));
            executeme.add(new AWXGattRequest(gatt, cstatus, null, AWXGattRequest.MODE_ENABLE_NOTIFICATION));

            executeNext(gatt);
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

                    String friendly = AWXDevices.getFriendlyName(val);
                    Log.d(LOGTAG, " onCharacteristicRead tag=" + tag + " val=" + val + " friendly=" + friendly);

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

                case AWXDefs.CHARACTERISTIC_MESH_LIGHT_STATUS:
                    tag = "light_status";
                    val = getBytesToHexString(data, 0, data.length);
                    break;

                case AWXDefs.CHARACTERISTIC_MESH_LIGHT_COMMAND:
                    tag = "light_command";
                    val = getBytesToHexString(data, 0, data.length);
                    break;

                case AWXDefs.CHARACTERISTIC_MESH_LIGHT_OTA:
                    tag = "light_ota";
                    val = getBytesToHexString(data, 0, data.length);
                    break;

                case AWXDefs.CHARACTERISTIC_MESH_LIGHT_PAIR:
                    tag = "light_pair";
                    val = getBytesToHexString(data, 0, data.length);

                    if (data[0] == 13)
                    {
                        if (data[0] == 13)
                        {
                            byte[] sessionRandom = new byte[8];
                            System.arraycopy(data, 1, sessionRandom, 0, sessionRandom.length);

                            byte[] meshName = Arrays.copyOf(mMeshNameStr.getBytes(), 16);
                            byte[] meshPassword = Arrays.copyOf(mMeshPasswordStr.getBytes(), 16);

                            mSessionKey = AWXProtocol.getSessionKey(meshName, meshPassword, mSessionRandom, sessionRandom);

                            Log.e(LOGTAG, "onCharacteristicRead: mSessionKey=" + getBytesToHexString(mSessionKey, 0, mSessionKey.length));
                            Log.e(LOGTAG, "onCharacteristicRead: paired!!!!!!");

                            setColor(gatt, ccommand, 0x880000);
                        }
                    }
                    else
                    {
                        if (data[0] == 14)
                        {
                            Log.e(LOGTAG, "onCharacteristicRead: OPCODE_ENC_FAIL !");
                        }
                        else
                        {
                            if (data[0] == 7)
                            {
                                Log.e(LOGTAG, "onCharacteristicRead: IRGENDWAS !");
                            }
                        }
                    }

                    break;
            }

            Log.d(LOGTAG, "onCharacteristicRead: tag=" + tag + " val=" + val);

            executeNext(gatt);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int status)
        {
            Log.d(LOGTAG, "onCharacteristicWrite: status=" + status + " chara=" + chara.getUuid());

            executeNext(gatt);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic chara)
        {
            Log.d(LOGTAG, "onCharacteristicChanged: chara=" + chara.getUuid());

            executeNext(gatt);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            Log.d(LOGTAG, "onDescriptorRead: descriptor=" + descriptor.getUuid());

            executeNext(gatt);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status)
        {
            Log.d(LOGTAG, "onDescriptorWrite: descriptor=" + descriptor.getUuid());

            executeNext(gatt);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private class AWXGattRequest
    {
        public static final int MODE_WRITE_CHARACTERISTIC = 0;
        public static final int MODE_READ_CHARACTERISTIC = 1;
        public static final int MODE_ENABLE_NOTIFICATION = 2;
        public static final int MODE_DISABLE_NOTIFICATION = 3;

        public BluetoothGatt gatt;
        public BluetoothGattCharacteristic chara;
        public byte[] data;
        public int mode;

        public AWXGattRequest(BluetoothGatt gatt, BluetoothGattCharacteristic chara, byte[] data, int mode)
        {
            this.gatt = gatt;
            this.chara = chara;
            this.data = data;
            this.mode = mode;
        }
    }

    private AWXGattRequest currentRequest;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void executeNext(BluetoothGatt gatt)
    {
        ArrayList<AWXGattRequest> readme = gatt2execute.get(gatt);
        if ((readme == null) || (readme.size() == 0)) return;

        currentRequest = readme.remove(0);

        if (currentRequest.mode == AWXGattRequest.MODE_READ_CHARACTERISTIC)
        {
            gatt.readCharacteristic(currentRequest.chara);
        }

        if (currentRequest.mode == AWXGattRequest.MODE_WRITE_CHARACTERISTIC)
        {
            Log.d(LOGTAG, "executeNext: write: data="
                    + getBytesToHexString(currentRequest.data, 0, currentRequest.data.length));

            currentRequest.chara.setValue(currentRequest.data);
            currentRequest.chara.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            gatt.writeCharacteristic(currentRequest.chara);
        }

        if (currentRequest.mode == AWXGattRequest.MODE_ENABLE_NOTIFICATION)
        {
            if (gatt.setCharacteristicNotification(currentRequest.chara,true))
            {
                BluetoothGattDescriptor descriptor = currentRequest.chara.getDescriptor(AWXDefs.UUID_NOTIFICATION_DESCRIPTOR);

                if (descriptor != null)
                {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }

            }
        }

        if (currentRequest.mode == AWXGattRequest.MODE_DISABLE_NOTIFICATION)
        {
            if (gatt.setCharacteristicNotification(currentRequest.chara,false))
            {
                BluetoothGattDescriptor descriptor = currentRequest.chara.getDescriptor(AWXDefs.UUID_NOTIFICATION_DESCRIPTOR);

                if (descriptor != null)
                {
                    descriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
            }
        }
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setColor(BluetoothGatt gatt, BluetoothGattCharacteristic chara, int color)
    {
        byte[] data = AWXProtocol.getValue(meshId, (byte) -30, new byte[]{(byte) 4, (byte) Color.red(color), (byte) Color.green(color), (byte) Color.blue(color)});

        Log.d(LOGTAG, "setColor: plain=" + getBytesToHexString(data, 0, data.length));

        byte[] cryp = AWXProtocol.encryptValue(macaddr, mSessionKey, data);

        Log.d(LOGTAG, "setColor: crypt=" + getBytesToHexString(cryp, 0, cryp.length));

        executeme.add(new AWXGattRequest(gatt, chara, cryp, AWXGattRequest.MODE_WRITE_CHARACTERISTIC));
    }

    public static short getMeshId(byte[] manufData)
    {
        if (manufData == null || manufData.length <= 12)
        {
            Log.e(LOGTAG, "getMeshId: manufData not correct : " + manufData);

            return (short) 0;
        }

        Log.d(LOGTAG, "getMeshId: fund");

        return (short) ((manufData[12] << 8) | (manufData[11] & 255));
    }
}
