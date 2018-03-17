package de.xavaro.android.iot.proxim;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.simple.Simple;

public class IOTProximServer
{
    private static final String LOGTAG = IOTProximServer.class.getName();

    private final static int MANUFACTURER_ID = 4711;

    private byte reserved;
    private byte powerLevel;
    private UUID serverUUID;
    private ArrayList<BluetoothDevice> connectedDevices;
    private BluetoothGattCharacteristic proximCharacteristic;

    private BluetoothGattServer btGattServer;
    private BluetoothLeAdvertiser btLEAdvertiser;

    static public void startService(Context context)
    {
        if (IOT.instance == null) return;

        if (IOT.instance.proximServer == null)
        {
            IOT.instance.proximServer = new IOTProximServer(context);
        }
    }

    public IOTProximServer(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            return;
        }

        BluetoothManager btManager = Simple.getBTManager();
        BluetoothAdapter btAdapter = Simple.getBTAdapter();

        if ((btManager == null) || (btAdapter == null))
        {
            return;
        }

        if ((IOT.device == null) || (IOT.device.uuid == null))
        {
            return;
        }

        serverUUID = UUID.fromString(IOT.device.uuid);
        powerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;

        btGattServer = btManager.openGattServer(context, btGattServerCallback);
        btLEAdvertiser = btAdapter.getBluetoothLeAdvertiser();

        connectedDevices = new ArrayList<>();

        initializeServer();
        startAdvertising();
    }

    public void close()
    {
        stopAdvertising();
        shutdownServer();
    }

    private void initializeServer()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            BluetoothGattService service = new BluetoothGattService(
                    serverUUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

            int properties = BluetoothGattCharacteristic.PROPERTY_READ
                    | BluetoothGattCharacteristic.PROPERTY_INDICATE;

            proximCharacteristic = new BluetoothGattCharacteristic(
                    IOTProximMessage.CHARACTERISTIC_UUID, properties,
                    BluetoothGattCharacteristic.PERMISSION_READ);

            service.addCharacteristic(proximCharacteristic);

            btGattServer.addService(service);

            Log.d(LOGTAG, "initializeServer: service added.");
        }
    }

    private void startAdvertising()
    {
        if (btLEAdvertiser == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                    .setConnectable(true)
                    .setTimeout(0)
                    .build();

            byte[] mdata = new byte[]{ 'I', 'O', 'T', 'D', powerLevel, reserved };

            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .setIncludeTxPowerLevel(false)
                    .addManufacturerData(MANUFACTURER_ID, mdata)
                    .addServiceUuid(new ParcelUuid(serverUUID))
                    .build();

            Log.d(LOGTAG, "startAdvertising: data=" + data.toString());

            btLEAdvertiser.startAdvertising(settings, data, btAdvertiseCallback);
        }
    }

    private final Object mLock = new Object();
    private IOTProximMessage mMessage;

    public void addMessage(IOTProximMessage message)
    {
        synchronized (mLock)
        {
            mMessage = message;
        }

        notifyConnectedDevices();
    }

    public byte[] getMessage()
    {
        synchronized (mLock)
        {
            if (mMessage != null)
            {
                return mMessage.getBytes();
            }
            else
            {
                return new byte[0];
            }
        }
    }

    private void stopAdvertising()
    {
        if (btLEAdvertiser == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            btLEAdvertiser.stopAdvertising(btAdvertiseCallback);
        }
    }

    private final AdvertiseCallback btAdvertiseCallback = new AdvertiseCallback()
    {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect)
        {
            Log.d(LOGTAG, "AdvertiseCallback: onStartSuccess.");
        }

        @Override
        public void onStartFailure(int errorCode)
        {
            String desc = "unknown";

            if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED)
                desc = "ADVERTISE_FAILED_FEATURE_UNSUPPORTED";

            if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS)
                desc = "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS";

            if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED)
                desc = "ADVERTISE_FAILED_ALREADY_STARTED";

            if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE)
                desc = "ADVERTISE_FAILED_DATA_TOO_LARGE";

            if (errorCode == AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR)
                desc = "ADVERTISE_FAILED_INTERNAL_ERROR";

            Log.e(LOGTAG, "AdvertiseCallback: onStartFailure"
                    + " err=" + errorCode
                    + " desc=" + desc);
        }
    };

    private void shutdownServer()
    {
        if (btGattServer == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            btGattServer.close();
        }
    }

    private final BluetoothGattServerCallback btGattServerCallback = new BluetoothGattServerCallback()
    {
        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState)
        {
            super.onConnectionStateChange(device, status, newState);

            Log.d(LOGTAG, "onConnectionStateChange "
                    + Simple.getBTStatusDescription(status) + " "
                    + Simple.getBTStateDescription(newState));

            if (newState == BluetoothProfile.STATE_CONNECTED)
            {
                Log.d(LOGTAG, "Device Connected: " + device.getName());

                connectedDevices.add(device);
            }

            if (newState == BluetoothProfile.STATE_DISCONNECTED)
            {
                Log.d(LOGTAG, "Device Disconnected: " + device.getName());

                connectedDevices.remove(device);
            }
        }

        @Override
        public void onCharacteristicReadRequest(
                BluetoothDevice device, int requestId, int offset,
                BluetoothGattCharacteristic characteristic)
        {
            super.onCharacteristicReadRequest(device, requestId, offset, characteristic);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                if (characteristic == proximCharacteristic)
                {
                    btGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, getMessage());
                }
                else
                {
                    btGattServer.sendResponse(device, requestId, BluetoothGatt.GATT_FAILURE, 0, null);
                }
            }
        }

        @Override
        public void onCharacteristicWriteRequest(
                BluetoothDevice device, int requestId,
                BluetoothGattCharacteristic characteristic,
                boolean preparedWrite, boolean responseNeeded,
                int offset, byte[] value)
        {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value);
        }
    };

    private void notifyConnectedDevices()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            for (BluetoothDevice device : connectedDevices)
            {
                if (proximCharacteristic != null)
                {
                    proximCharacteristic.setValue(getMessage());

                    btGattServer.notifyCharacteristicChanged(device, proximCharacteristic, false);
                }
            }
        }
    }
}