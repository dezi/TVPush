package de.xavaro.android.iot.proxim;

import java.nio.ByteBuffer;
import java.util.UUID;

public class IOTProximMessage
{
    public static UUID CHARACTERISTIC_UUID = UUID.fromString("ba29606c-6dfa-443d-961c-f4403b788a10");

    static public final int SIZE = 2 + 8 + 8; // Short.BYTES + Double.BYTES + Double.BYTES;

    public byte major = 1;
    public byte minor = 0;
    public double lat = 53.568187;
    public double lon = 10.140207;

    public IOTProximMessage()
    {
    }

    public IOTProximMessage(double lat, double lon)
    {
        this.lat = lat;
        this.lon = lon;
    }

    public byte[] getBytes()
    {
        return ByteBuffer
                .allocate(IOTProximMessage.SIZE)
                .put(major)
                .put(minor)
                .putDouble(lat)
                .putDouble(lon)
                .array();
    }
}
