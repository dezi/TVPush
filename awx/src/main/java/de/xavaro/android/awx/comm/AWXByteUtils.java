package de.xavaro.android.awx.comm;

import android.graphics.Color;
import android.support.v4.internal.view.SupportMenu;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class AWXByteUtils
{
    public static byte[] shortToBytes(ByteOrder byteOrder, short value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(byteOrder);
        buffer.putShort(value);
        return buffer.array();
    }

    public static byte[] colorToBytes(int value)
    {
        return new byte[]{(byte) Color.red(value), (byte) Color.green(value), (byte) Color.blue(value)};
    }

    public static byte[] colorsToBytes(ArrayList<Integer> value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(value.size() * 3);
        for (int i = 0; i < value.size(); i++)
        {
            buffer.put(colorToBytes(((Integer) value.get(i)).intValue()));
        }
        return buffer.array();
    }

    public static byte[] intToBytes(ByteOrder byteOrder, int value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(byteOrder);
        buffer.putInt(value);
        return buffer.array();
    }

    public static short bytesToShort(ByteOrder byteOrder, byte[] array)
    {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(byteOrder);
        return buffer.getShort();
    }

    public static int bytesToColor(byte[] array)
    {
        return Color.rgb(array[0] & 255, array[1] & 255, array[2] & 255);
    }

    public static ArrayList<Integer> bytesToColors(byte[] array)
    {
        ArrayList<Integer> colors = new ArrayList();
        for (int i = 0; i < array.length / 3; i++)
        {
            byte[] tmp = new byte[3];
            System.arraycopy(array, i * 3, tmp, 0, 3);
            colors.add(Integer.valueOf(bytesToColor(tmp)));
        }
        return colors;
    }

    public static int bytesToInt(ByteOrder byteOrder, byte[] array)
    {
        ByteBuffer buffer = ByteBuffer.wrap(array);
        buffer.order(byteOrder);
        return buffer.getInt();
    }

    public static int bytesToInt(byte[] array)
    {
        return bytesToInt(array[0], array[1]);
    }

    public static int bytesToInt(byte b1, byte b2)
    {
        return ((b1 & 255) << 8) | (b2 & 255);
    }

    public static byte[][] split(byte[] array, byte[] header)
    {
        byte[][] result = new byte[0][];
        int lastIndexOfHeader = -1;
        for (int i = 0; i <= array.length - header.length; i++)
        {
            boolean found = true;
            for (int j = 0; j < header.length; j++)
            {
                int i2;
                if (array[i + j] == header[j])
                {
                    i2 = 1;
                }
                else
                {
                    i2 = 0;
                }
                found &= i2 == 1;
            }
            if (found)
            {
                if (lastIndexOfHeader > -1)
                {
                    int length = i - lastIndexOfHeader;
                    byte[] bytes = new byte[length];
                    System.arraycopy(array, lastIndexOfHeader, bytes, 0, length);
                    result = append(result, bytes);
                }
                lastIndexOfHeader = i;
            }
        }
        if (lastIndexOfHeader <= -1)
        {
            return result;
        }
        int length = array.length - lastIndexOfHeader;
        byte[] bytes = new byte[length];
        System.arraycopy(array, lastIndexOfHeader, bytes, 0, length);
        return append(result, bytes);
    }

    private static byte[][] append(byte[][] array, byte[] value)
    {
        byte[][] result = new byte[(array.length + 1)][];
        System.arraycopy(array, 0, result, 0, array.length);
        result[array.length] = value;
        return result;
    }

    public static byte[] toByteArray(String str, int capacity)
    {
        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        int length = Math.min(str.length(), capacity);
        for (int i = 0; i < length; i++)
        {
            buffer.put((byte) str.charAt(i));
        }
        return buffer.array();
    }

    public static byte[] reverse(byte[] array)
    {
        byte[] result = new byte[array.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = array[(array.length - 1) - i];
        }
        return result;
    }

    public static byte[] xor(byte[] array1, byte[] array2) throws IllegalArgumentException
    {
        if (array1.length != array2.length)
        {
            throw new IllegalArgumentException("Invalid length");
        }
        byte[] result = new byte[array1.length];
        for (int i = 0; i < result.length; i++)
        {
            result[i] = (byte) (array1[i] ^ array2[i]);
        }
        return result;
    }

    public static int crc16(byte[] array)
    {
        int length = array.length - 2;
        short[] poly = new short[]{(short) 0, (short) -24575};
        int crc = SupportMenu.USER_MASK;
        for (int j = 0; j < length; j++)
        {
            int ds = array[j];
            for (int i = 0; i < 8; i++)
            {
                crc = (crc >> 1) ^ (poly[(crc ^ ds) & 1] & SupportMenu.USER_MASK);
                ds >>= 1;
            }
        }
        return crc;
    }

    public static void fillCrc(byte[] array, int crc)
    {
        int length = array.length - 2;
        int offset = length + 1;
        array[length] = (byte) (crc & 255);
        array[offset] = (byte) ((crc >> 8) & 255);
    }

    public static boolean getBitInByte(byte b, int position)
    {
        return (((int) Math.pow(2.0d, (double) position)) & b) != 0;
    }

    public static int getValueFromBitToBit(byte b, int start, int length)
    {
        int value = 0;
        for (int i = 0; i < length; i++)
        {
            value = (int) ((getBitInByte(b, start + i) ? Math.pow(2.0d, (double) i) : 0.0d) + ((double) value));
        }
        return value;
    }

    public static byte setBitInByte(byte b, int position, boolean value)
    {
        byte temp = (byte) 0;
        for (int i = 0; i < 8; i++)
        {
            if (i == position)
            {
                double pow;
                if (value)
                {
                    pow = Math.pow(2.0d, (double) i);
                }
                else
                {
                    pow = 0.0d;
                }
                temp = (byte) (((byte) ((int) pow)) + temp);
            }
            else
            {
                temp = (byte) (((byte) ((int) (getBitInByte(b, i) ? Math.pow(2.0d, (double) i) : 0.0d))) + temp);
            }
        }
        return temp;
    }

    public static byte setValueFromBitToBit(byte b, int start, int length, int value, int startValue)
    {
        byte temp = (byte) 0;
        int i = 0;
        while (i < 8)
        {
            if (i < start || i >= start + length)
            {
                temp = setBitInByte(temp, i, getBitInByte(b, i));
            }
            else
            {
                temp = setBitInByte(temp, i, (((int) Math.pow(2.0d, (double) ((i + startValue) - start))) & value) != 0);
            }
            i++;
        }
        return temp;
    }

    public static String getBytesArrayAsString(byte[] bytesArray)
    {
        String result = "";
        if (bytesArray == null)
        {
            return "null";
        }
        for (byte formatIntToHex : bytesArray)
        {
            result = result + " 0x" + formatIntToHex(formatIntToHex);
        }
        return result;
    }

    public static int formatIntToHex(int n)
    {
        return Integer.valueOf(String.valueOf(n), 16).intValue();
    }
}
