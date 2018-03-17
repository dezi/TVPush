package de.xavaro.android.iot.proxim;

import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.UUID;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.simple.Simple;

public class IOTProximServer
{
    private static final String LOGTAG = IOTProximServer.class.getName();

    private byte powerLevel;
    private AdvertiseSettings settings;
    private BluetoothLeAdvertiser btLEAdvertiser;

    private IOTProximCallback callbackGPSLowQuality;
    private IOTProximCallback callbackGPSHighQuality;
    private IOTProximCallback callbackIOTHuman;
    private IOTProximCallback callbackIOTDevice;

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

        powerLevel = AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM;
        btLEAdvertiser = btAdapter.getBluetoothLeAdvertiser();

        startAdvertising();
    }

    public void close()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            stopAdvertising();
        }
    }

    private void startAdvertising()
    {
        if (btLEAdvertiser == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_LOW_LATENCY)
                    .setTxPowerLevel(powerLevel)
                    .setConnectable(false)
                    .setTimeout(0)
                    .build();

            advertiseGPSLowQuality();
            advertiseGPSHighQuality();

            advertiseIOTHuman();
            advertiseIOTDevice();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopAdvertising()
    {
        if (btLEAdvertiser == null) return;

        if (callbackGPSLowQuality != null)
        {
            btLEAdvertiser.stopAdvertising(callbackGPSLowQuality);
            callbackGPSLowQuality = null;
        }

        if (callbackGPSHighQuality != null)
        {
            btLEAdvertiser.stopAdvertising(callbackGPSHighQuality);
            callbackGPSHighQuality = null;
        }

        if (callbackIOTHuman != null)
        {
            btLEAdvertiser.stopAdvertising(callbackIOTHuman);
            callbackIOTHuman = null;
        }

        if (callbackIOTDevice != null)
        {
            btLEAdvertiser.stopAdvertising(callbackIOTDevice);
            callbackIOTDevice = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void advertiseGPSLowQuality()
    {
        double lat = 53.568235;
        double lon = 10.140103;

        byte[] bytes = ByteBuffer
                .allocate(1 + 1 + 8 + 8)
                .put(IOTProxim.ADVERTISE_GPSLQ)
                .put(powerLevel)
                .putDouble(lat)
                .putDouble(lon)
                .array();

        callbackGPSLowQuality = advertiseDat(bytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void advertiseGPSHighQuality()
    {
        double lat = 53.568235;
        double lon = 10.140103;

        byte[] bytes = ByteBuffer
                .allocate(1 + 1 + 8 + 8)
                .put(IOTProxim.ADVERTISE_GPSHQ)
                .put(powerLevel)
                .putDouble(lat)
                .putDouble(lon)
                .array();

        callbackGPSHighQuality = advertiseDat(bytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void advertiseIOTHuman()
    {
        if ((IOT.human == null) || (IOT.human.uuid == null)) return;

        byte[] bytes = ByteBuffer
                .allocate(1 + 1 + 8 + 8)
                .put(IOTProxim.ADVERTISE_IOT_HUMAN)
                .put(powerLevel)
                .putLong(UUID.fromString(IOT.human.uuid).getMostSignificantBits())
                .putLong(UUID.fromString(IOT.human.uuid).getLeastSignificantBits())
                .array();

        callbackIOTHuman = advertiseDat(bytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void advertiseIOTDevice()
    {
        if ((IOT.device == null) || (IOT.device.uuid == null)) return;

        byte[] bytes = ByteBuffer
                .allocate(1 + 1 + 8 + 8)
                .put(IOTProxim.ADVERTISE_IOT_DEVICE)
                .put(powerLevel)
                .putLong(UUID.fromString(IOT.device.uuid).getMostSignificantBits())
                .putLong(UUID.fromString(IOT.device.uuid).getLeastSignificantBits())
                .array();

        callbackIOTDevice = advertiseDat(bytes);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private IOTProximCallback advertiseDat(byte[] bytes)
    {
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addManufacturerData(IOTProxim.MANUFACTURER_ID, bytes)
                .build();

        Log.d(LOGTAG, "advertiseDat: data=" + data.toString());

        IOTProximCallback callback = new IOTProximCallback();

        btLEAdvertiser.startAdvertising(settings, data, callback);

        return callback;
    }
}