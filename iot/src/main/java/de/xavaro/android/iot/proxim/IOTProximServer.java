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
import de.xavaro.android.iot.status.IOTStatus;

public class IOTProximServer
{
    private static final String LOGTAG = IOTProximServer.class.getSimpleName();

    private int powerLevel;
    private AdvertiseSettings settings;
    private BluetoothLeAdvertiser btLEAdvertiser;

    private IOTProximCallback callbackGPSCoarse;
    private IOTProximCallback callbackGPSFine;
    private IOTProximCallback callbackIOTHuman;
    private IOTProximCallback callbackIOTDevice;
    private IOTProximCallback callbackIOTDevname;

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
                    .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                    .setTxPowerLevel(powerLevel)
                    .setConnectable(true)
                    .setTimeout(0)
                    .build();

            advertiseGPSFine();
            advertiseGPSCoarse();
            advertiseIOTDevice();

            //advertiseIOTHuman();
            //advertiseIOTDevname();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void stopAdvertising()
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
            Float alt = null;

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
                        .put(IOTProxim.getEstimatedTxPowerFromPowerlevel(powerLevel))
                        .putDouble(lat)
                        .putDouble(lon)
                        .putFloat(alt)
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
            Float alt = null;

            if (IOT.device != null)
            {
                if ((IOT.device.fixedLatCoarse != null) && (IOT.device.fixedLonCoarse != null))
                {
                    lat = IOT.device.fixedLatCoarse;
                    lon = IOT.device.fixedLonCoarse;
                    alt = IOT.device.fixedAltCoarse;
                }
                else
                {
                    IOTStatus status = new IOTStatus(IOT.device.uuid);

                    if ((status.positionLatCoarse != null) && (status.positionLonCoarse != null))
                    {
                        lat = status.positionLatCoarse;
                        lon = status.positionLonCoarse;
                        alt = status.positionAltCoarse;
                    }
                }
            }

            if ((lat != null) && (lon != null))
            {
                byte[] bytes = ByteBuffer
                        .allocate(1 + 1 + 8 + 8 + 4)
                        .put(IOTProxim.ADVERTISE_GPS_COARSE)
                        .put(IOTProxim.getEstimatedTxPowerFromPowerlevel(powerLevel))
                        .putDouble(lat)
                        .putDouble(lon)
                        .putFloat(alt)
                        .array();

                callbackGPSCoarse = advertiseDat(bytes);

                Log.d(LOGTAG, "advertiseGPSCoarse: lat=" + lat + " lon=" + lon + " alt=" + alt);
            }
        }
    }

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
                    .put(IOTProxim.getEstimatedTxPowerFromPowerlevel(powerLevel))
                    .putLong(UUID.fromString(IOT.human.uuid).getMostSignificantBits())
                    .putLong(UUID.fromString(IOT.human.uuid).getLeastSignificantBits())
                    .array();

            callbackIOTHuman = advertiseDat(bytes);

            Log.d(LOGTAG, "advertiseIOTHuman: uuid=" + IOT.human.uuid);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                    .put(IOTProxim.getEstimatedTxPowerFromPowerlevel(powerLevel))
                    .putLong(UUID.fromString(IOT.device.uuid).getMostSignificantBits())
                    .putLong(UUID.fromString(IOT.device.uuid).getLeastSignificantBits())
                    .array();

            callbackIOTDevice = advertiseDat(bytes);

            Log.d(LOGTAG, "advertiseIOTDevice: uuid=" + IOT.device.uuid);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                    .put(IOTProxim.getEstimatedTxPowerFromPowerlevel(powerLevel))
                    .put(devbytes)
                    .array();

            callbackIOTDevname = advertiseDat(bytes);

            Log.d(LOGTAG, "advertiseIOTDevname: devname=" + devname);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private IOTProximCallback advertiseDat(byte[] bytes)
    {
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(false)
                .setIncludeTxPowerLevel(false)
                .addManufacturerData(IOTProxim.MANUFACTURER_IOT, bytes)
                .build();

        //Log.d(LOGTAG, "advertiseDat: data=" + data.toString());

        IOTProximCallback callback = new IOTProximCallback();

        btLEAdvertiser.startAdvertising(settings, data, callback);

        return callback;
    }
}