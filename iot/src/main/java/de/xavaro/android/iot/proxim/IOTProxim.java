package de.xavaro.android.iot.proxim;

import android.support.annotation.Nullable;

import android.bluetooth.le.AdvertiseCallback;

public class IOTProxim
{
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
}
