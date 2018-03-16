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
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.ParcelUuid;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import de.xavaro.android.iot.simple.Simple;

public class IOTProximServer
{
    private static final String LOGTAG = IOTProximServer.class.getName();

    public static UUID IOT_PROXIM_SERVICE_UUID = UUID.fromString("feedface-b00b-dead-code-beefaffedada");

    private ArrayList<BluetoothDevice> connectedDevices;
    private BluetoothGattCharacteristic proximCharacteristic;

    private BluetoothLeAdvertiser btLEAdvertiser;
    private BluetoothGattServer btGattServer;

    static public boolean checkBluetooth(Context context)
    {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Log.e(LOGTAG, "Bluetooth LE is not supported!");

            return false;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            Log.d(LOGTAG, "Android version too old!");

            return false;
        }

        BluetoothAdapter ba = Simple.getBTAdapter();

        if (ba == null)
        {
            Log.e(LOGTAG, "BluetoothAdapter ist not available!");

            return false;
        }

        if (!ba.isEnabled())
        {
            Log.w(LOGTAG, "BluetoothAdapter is not enabled!");

            ba.enable();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (!ba.isMultipleAdvertisementSupported())
            {
                Log.d(LOGTAG, "No Multiple Advertisement Support!");
            }
        }

        return ba.isEnabled();
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

    private void startAdvertising()
    {
        if (btLEAdvertiser == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            AdvertiseSettings settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_LOW)
                    .setConnectable(true)
                    .setTimeout(0)
                    .build();

            AdvertiseData data = new AdvertiseData.Builder()
                    .setIncludeDeviceName(false)
                    .setIncludeTxPowerLevel(true)
                    .addServiceData(new ParcelUuid(IOT_PROXIM_SERVICE_UUID), "IOTProxim".getBytes())
                    .addServiceUuid(new ParcelUuid(IOT_PROXIM_SERVICE_UUID))
                    .build();

            btLEAdvertiser.startAdvertising(settings, data, btAdvertiseCallback);
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
            Log.e(LOGTAG, "AdvertiseCallback: onStartFailure err=" + errorCode);
        }
    };

    private void initializeServer()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            BluetoothGattService service = new BluetoothGattService(
                    IOT_PROXIM_SERVICE_UUID, BluetoothGattService.SERVICE_TYPE_PRIMARY);

            int properties = BluetoothGattCharacteristic.PROPERTY_READ
                    | BluetoothGattCharacteristic.PROPERTY_INDICATE;

            proximCharacteristic = new BluetoothGattCharacteristic(
                    IOTProximMessage.CHARACTERISTIC_UUID, properties,
                    BluetoothGattCharacteristic.PERMISSION_READ);

            service.addCharacteristic(proximCharacteristic);

            btGattServer.addService(service);
        }
    }

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