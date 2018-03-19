package de.xavaro.android.iot.proxim;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.AdvertiseSettings;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.Nullable;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import android.bluetooth.le.AdvertiseCallback;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import de.xavaro.android.iot.simple.Simple;

public class IOTProxim
{
    private static final String LOGTAG = IOTProxim.class.getSimpleName();

    public final static int IOT_MANUFACTURER_ID = 4711;

    public final static byte ADVERTISE_GPS_FINE = 1;
    public final static byte ADVERTISE_GPS_COARSE = 2;

    public final static byte ADVERTISE_IOT_HUMAN = 3;
    public final static byte ADVERTISE_IOT_DOMAIN = 4;
    public final static byte ADVERTISE_IOT_DEVICE = 5;
    public final static byte ADVERTISE_IOT_LOCATION = 6;

    public static String getAdvertiseType(int type)
    {
        switch (type)
        {
            case ADVERTISE_GPS_FINE: return "ADVERTISE_GPS_FINE";
            case ADVERTISE_GPS_COARSE: return "ADVERTISE_GPS_COARSE";

            case ADVERTISE_IOT_HUMAN: return "ADVERTISE_IOT_HUMAN";
            case ADVERTISE_IOT_DOMAIN: return "ADVERTISE_IOT_DOMAIN";
            case ADVERTISE_IOT_DEVICE: return "ADVERTISE_IOT_DEVICE";
            case ADVERTISE_IOT_LOCATION: return "ADVERTISE_IOT_LOCATION";
        }

        return "UNKNOWN_TYPE=" + type;
    }

    @Nullable
    public static String getAdvertiseVendor(int vendor)
    {
        switch (vendor)
        {
            case 76: return "Apple, Inc.";
            case 117: return "Samsung Electronics Co. Ltd.";
            case 224: return "Google";
            case 301: return "Sony Corporation";

            case 4711: return "IOT";
        }

        return null;
    }

    public static String getBTAdvertiserFailDescription(int error)
    {
        switch (error)
        {
            case AdvertiseCallback.ADVERTISE_FAILED_FEATURE_UNSUPPORTED:
                return "ADVERTISE_FAILED_FEATURE_UNSUPPORTED";

            case AdvertiseCallback.ADVERTISE_FAILED_TOO_MANY_ADVERTISERS:
                return "ADVERTISE_FAILED_TOO_MANY_ADVERTISERS";

            case AdvertiseCallback.ADVERTISE_FAILED_ALREADY_STARTED:
                return "ADVERTISE_FAILED_ALREADY_STARTED";

            case AdvertiseCallback.ADVERTISE_FAILED_DATA_TOO_LARGE:
                return "ADVERTISE_FAILED_DATA_TOO_LARGE";

            case AdvertiseCallback.ADVERTISE_FAILED_INTERNAL_ERROR:
                return "ADVERTISE_FAILED_INTERNAL_ERROR";
        }

        return "UNKNOWN_ERROR=" + error;
    }

    public static void checkBTPermissions(Context appcontent)
    {
        BluetoothAdapter adapter = Simple.getBTAdapter();

        boolean btAdapter = (adapter != null);
        boolean btAdapterEnabled = btAdapter && adapter.enable();

        boolean bt = ContextCompat.checkSelfPermission(appcontent, Manifest.permission.BLUETOOTH)
                == PackageManager.PERMISSION_GRANTED;

        boolean bt_admin = ContextCompat.checkSelfPermission(appcontent, Manifest.permission.BLUETOOTH_ADMIN)
                == PackageManager.PERMISSION_GRANTED;

        boolean gps_fine = ContextCompat.checkSelfPermission(appcontent, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        boolean gps_coarse = ContextCompat.checkSelfPermission(appcontent, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        LocationManager locationManager = Simple.getLocationManager();

        boolean locman = (locationManager != null);
        boolean locgps = false;
        boolean locnet = false;

        if (locman)
        {
            try
            {
                locgps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            catch (Exception ignore)
            {
            }

            try
            {
                locnet = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            catch (Exception ignore)
            {
            }
        }

        Log.d(LOGTAG, "checkBTPermissions: ADAPTER=" + btAdapter);
        Log.d(LOGTAG, "checkBTPermissions: ADAPTER_ENABLED=" + btAdapterEnabled);

        Log.d(LOGTAG, "checkBTPermissions: BLUETOOTH=" + bt);
        Log.d(LOGTAG, "checkBTPermissions: BLUETOOTH_ADMIN=" + bt_admin);
        Log.d(LOGTAG, "checkBTPermissions: ACCESS_FINE_LOCATION=" + gps_fine);
        Log.d(LOGTAG, "checkBTPermissions: ACCESS_COARSE_LOCATION=" + gps_coarse);

        Log.d(LOGTAG, "checkBTPermissions: LOCATION=" + locman);
        Log.d(LOGTAG, "checkBTPermissions: GPS_PROVIDER=" + locgps);
        Log.d(LOGTAG, "checkBTPermissions: NETWORK_PROVIDER=" + locnet);
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
