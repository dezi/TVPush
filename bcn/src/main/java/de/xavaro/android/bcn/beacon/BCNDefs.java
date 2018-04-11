package de.xavaro.android.bcn.beacon;

import android.support.annotation.Nullable;

import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseSettings;

public class BCNDefs
{
    private static final String LOGTAG = BCNDefs.class.getSimpleName();

    public final static int MANUFACTURER_IOT = 4711;
    public final static int MANUFACTURER_APPLE = 76;
    public final static int MANUFACTURER_SAMSUNG = 117;
    public final static int MANUFACTURER_GOOGLE = 224;
    public final static int MANUFACTURER_SONY = 301;

    public final static byte ADVERTISE_GPS_FINE = 1;
    public final static byte ADVERTISE_GPS_COARSE = 2;

    public final static byte ADVERTISE_IOT_HUMAN = 3;
    public final static byte ADVERTISE_IOT_DOMAIN = 4;
    public final static byte ADVERTISE_IOT_DEVICE = 5;
    public final static byte ADVERTISE_IOT_LOCATION = 6;
    public final static byte ADVERTISE_IOT_DEVNAME = 7;

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

            case ADVERTISE_IOT_DEVNAME: return "ADVERTISE_IOT_DEVNAME";
        }

        return "UNKNOWN_TYPE=" + type;
    }

    @Nullable
    public static String getAdvertiseVendor(int vendor)
    {
        switch (vendor)
        {
            case MANUFACTURER_IOT: return "IOT";
            case MANUFACTURER_GOOGLE: return "Google";
            case MANUFACTURER_APPLE: return "Apple, Inc.";
            case MANUFACTURER_SONY: return "Sony Corporation";
            case MANUFACTURER_SAMSUNG: return "Samsung Electronics Co. Ltd.";
        }

        return null;
    }
}
