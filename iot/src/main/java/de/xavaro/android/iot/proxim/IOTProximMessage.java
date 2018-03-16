package de.xavaro.android.iot.proxim;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public class IOTProximMessage
{
    public static UUID CHARACTERISTIC_UUID = UUID.fromString("decafbad-f5d0-4cc0-aeb8-d00db16b00b5");

    static public final int SIZE = 8 + 4 + 4 + 1; //Long.BYTES + Float.BYTES*2 + Byte.BYTES;

    public int version = 1;
    public long timestamp = 0;
    public float distance = 0.0f;
    public float filtered = 0.0f;
    public boolean jump = false;

    public IOTProximMessage(float distance, float filtered, boolean jump)
    {
        this.timestamp = System.currentTimeMillis();
        this.distance = distance;
        this.filtered = filtered;
        this.jump = jump;
    }

    public byte[] getBytes()
    {
        byte[] b = ByteBuffer.allocate(IOTProximMessage.SIZE).
                order(ByteOrder.LITTLE_ENDIAN).putLong(timestamp).
                putFloat(distance).putFloat(filtered).
                put((byte) (jump ? 1 : 0)).array();
        return b;
    }

}
