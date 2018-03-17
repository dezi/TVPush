package de.xavaro.android.iot.proxim;

import android.bluetooth.BluetoothProfile;
import android.support.annotation.Nullable;

public class IOTProxim
{
    public final static int MANUFACTURER_ID = 4711;


    public final static byte ADVERTISE_GPSLQ = 1;
    public final static byte ADVERTISE_GPSHQ = 2;
    public final static byte ADVERTISE_IOT_HUMAN = 3;
    public final static byte ADVERTISE_IOT_DOMAIN = 4;
    public final static byte ADVERTISE_IOT_DEVICE = 5;
    public final static byte ADVERTISE_IOT_LOCATION = 6;

    public static String getAdvertiseType(int type)
    {
        switch (type)
        {
            case ADVERTISE_GPSLQ: return "ADVERTISE_GPSLQ";
            case ADVERTISE_GPSHQ: return "ADVERTISE_GPSHQ";
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
}
