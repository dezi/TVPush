package de.xavaro.android.iot.proxim;

import android.support.annotation.RequiresApi;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;

import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;

import android.os.Build;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.UUID;

import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.base.IOT;

public class IOTAdvertiser
{
    private static final String LOGTAG = IOTAdvertiser.class.getSimpleName();

    private int powerLevel;
    private AdvertiseSettings settings;
    private BluetoothLeAdvertiser btLEAdvertiser;

    private IOTAdvertiserCallback callbackGPSCoarse;
    private IOTAdvertiserCallback callbackGPSFine;
    private IOTAdvertiserCallback callbackIOTHuman;
    private IOTAdvertiserCallback callbackIOTDevice;
    private IOTAdvertiserCallback callbackIOTDevname;

    static public void startService()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if ((IOT.instance != null) && (IOT.instance.proximServer == null))
            {
                IOT.instance.proximServer = new IOTAdvertiser();
                IOT.instance.proximServer.startAdvertising();
            }
        }
    }

    static public void stopService()
    {
        if ((IOT.instance != null) && (IOT.instance.proximServer != null))
        {
            IOT.instance.proximServer.stopAdvertising();
            IOT.instance.proximServer = null;
        }
    }

    private IOTAdvertiser()
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
    }

    private void startAdvertising()
    {
        if (btLEAdvertiser == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            settings = new AdvertiseSettings.Builder()
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(powerLevel)
                    .setConnectable(false)
                    .setTimeout(0)
                    .build();

            advertiseGPSFine();
            advertiseGPSCoarse();
            advertiseIOTDevice();

            //advertiseIOTHuman();
            //advertiseIOTDevname();
        }
    }

    private void stopAdvertising()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (btLEAdvertiser == null) return;

            if (callbackGPSFine != null)
            {
                btLEAdvertiser.stopAdvertising(callbackGPSFine);
                callbackGPSFine = null;
            }

            if (callbackGPSCoarse != null)
            {
                btLEAdvertiser.stopAdvertising(callbackGPSCoarse);
                callbackGPSCoarse = null;
            }

            if (callbackIOTDevice != null)
            {
                btLEAdvertiser.stopAdvertising(callbackIOTDevice);
                callbackIOTDevice = null;
            }

            if (callbackIOTHuman != null)
            {
                btLEAdvertiser.stopAdvertising(callbackIOTHuman);
                callbackIOTHuman = null;
            }

            if (callbackIOTDevname != null)
            {
                btLEAdvertiser.stopAdvertising(callbackIOTDevname);
                callbackIOTDevname = null;
            }
        }
    }

    public void advertiseGPSFine()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (callbackGPSFine != null)
            {
                btLEAdvertiser.stopAdvertising(callbackGPSFine);
                callbackGPSFine = null;
            }

            Double lat = null;
            Double lon = null;
            Double alt = null;

            if (IOT.device != null)
            {
                if ((IOT.device.fixedLatFine != null) && (IOT.device.fixedLonFine != null))
                {
                    lat = IOT.device.fixedLatFine;
                    lon = IOT.device.fixedLonFine;
                    alt = IOT.device.fixedAltFine;
                }
                else
                {
                    IOTStatus status = new IOTStatus(IOT.device.uuid);

                    if ((status.positionLatFine != null) && (status.positionLonFine != null))
                    {
                        lat = status.positionLatFine;
                        lon = status.positionLonFine;
                        alt = status.positionAltFine;
                    }
                }
            }

            if ((lat != null) && (lon != null))
            {
                byte[] bytes = ByteBuffer
                        .allocate(1 + 1 + 8 + 8 + 4)
                        .put(IOTProxim.ADVERTISE_GPS_FINE)
                        .put(getEstimatedTxPowerFromPowerlevel(powerLevel))
                        .putDouble(lat)
                        .putDouble(lon)
                        .putFloat((float) (double) alt)
                        .array();

                callbackGPSFine = advertiseDat(bytes);

                Log.d(LOGTAG, "advertiseGPSFine: lat=" + lat + " lon=" + lon + " alt=" + alt);
            }
        }
    }

    public void advertiseGPSCoarse()
    {
        if (callbackGPSFine != null)
        {
            //
            // No need to publish a coarse location
            // if we have a fine location.
            //

            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (callbackGPSCoarse != null)
            {
                btLEAdvertiser.stopAdvertising(callbackGPSCoarse);
                callbackGPSCoarse = null;
            }

            Double lat = null;
            Double lon = null;
            Double alt = null;

            if (IOT.device != null)
            {
                IOTStatus status = new IOTStatus(IOT.device.uuid);

                if ((status.positionLatCoarse != null) && (status.positionLonCoarse != null))
                {
                    lat = status.positionLatCoarse;
                    lon = status.positionLonCoarse;
                    alt = status.positionAltCoarse;
                }
            }

            if (lat != null)
            {
                byte[] bytes = ByteBuffer
                        .allocate(1 + 1 + 8 + 8 + 4)
                        .put(IOTProxim.ADVERTISE_GPS_COARSE)
                        .put(getEstimatedTxPowerFromPowerlevel(powerLevel))
                        .putDouble(lat)
                        .putDouble(lon)
                        .putFloat((float) (double) alt)
                        .array();

                callbackGPSCoarse = advertiseDat(bytes);

                Log.d(LOGTAG, "advertiseGPSCoarse: lat=" + lat + " lon=" + lon + " alt=" + alt);
            }
        }
    }

    @SuppressWarnings("unused")
    public void advertiseIOTHuman()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (callbackIOTHuman != null)
            {
                btLEAdvertiser.stopAdvertising(callbackIOTHuman);
                callbackIOTHuman = null;
            }

            if ((IOT.human == null) || (IOT.human.uuid == null)) return;

            byte[] bytes = ByteBuffer
                    .allocate(1 + 1 + 8 + 8)
                    .put(IOTProxim.ADVERTISE_IOT_HUMAN)
                    .put(getEstimatedTxPowerFromPowerlevel(powerLevel))
                    .putLong(UUID.fromString(IOT.human.uuid).getMostSignificantBits())
                    .putLong(UUID.fromString(IOT.human.uuid).getLeastSignificantBits())
                    .array();

            callbackIOTHuman = advertiseDat(bytes);

            Log.d(LOGTAG, "advertiseIOTHuman: uuid=" + IOT.human.uuid);
        }
    }

    public void advertiseIOTDevice()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (callbackIOTDevice != null)
            {
                btLEAdvertiser.stopAdvertising(callbackIOTDevice);
                callbackIOTDevice = null;
            }

            if ((IOT.device == null) || (IOT.device.uuid == null)) return;

            byte[] bytes = ByteBuffer
                    .allocate(1 + 1 + 8 + 8)
                    .put(IOTProxim.ADVERTISE_IOT_DEVICE)
                    .put(getEstimatedTxPowerFromPowerlevel(powerLevel))
                    .putLong(UUID.fromString(IOT.device.uuid).getMostSignificantBits())
                    .putLong(UUID.fromString(IOT.device.uuid).getLeastSignificantBits())
                    .array();

            callbackIOTDevice = advertiseDat(bytes);

            Log.d(LOGTAG, "advertiseIOTDevice: uuid=" + IOT.device.uuid);
        }
    }

    @SuppressWarnings("unused")
    public void advertiseIOTDevname()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (callbackIOTDevname != null)
            {
                btLEAdvertiser.stopAdvertising(callbackIOTDevname);
                callbackIOTDevname = null;
            }

            String devname = Simple.getDeviceUserName();
            if (devname.length() > 20) devname = devname.substring(0, 20);

            byte[] devbytes = devname.getBytes();

            byte[] bytes = ByteBuffer
                    .allocate(1 + 1 + devbytes.length)
                    .put(IOTProxim.ADVERTISE_IOT_DEVNAME)
                    .put(getEstimatedTxPowerFromPowerlevel(powerLevel))
                    .put(devbytes)
                    .array();

            callbackIOTDevname = advertiseDat(bytes);

            Log.d(LOGTAG, "advertiseIOTDevname: devname=" + devname);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private IOTAdvertiserCallback advertiseDat(byte[] bytes)
    {
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addManufacturerData(IOTProxim.MANUFACTURER_IOT, bytes)
                .build();

        //Log.d(LOGTAG, "advertiseDat: data=" + data.toString());

        IOTAdvertiserCallback callback = new IOTAdvertiserCallback();

        btLEAdvertiser.startAdvertising(settings, data, callback);

        return callback;
    }
    
    public static byte getEstimatedTxPowerFromPowerlevel(int powerlevel)
    {
        switch (powerlevel)
        {
            case AdvertiseSettings.ADVERTISE_TX_POWER_ULTRA_LOW: return -40;
            case AdvertiseSettings.ADVERTISE_TX_POWER_LOW: return -30;
            case AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM: return -23;
            case AdvertiseSettings.ADVERTISE_TX_POWER_HIGH: return -15;
        }

        return -23;
    }
}