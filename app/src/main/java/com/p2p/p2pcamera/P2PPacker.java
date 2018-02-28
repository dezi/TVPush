package com.p2p.p2pcamera;

@SuppressWarnings({"PointlessBitwiseExpression", "PointlessArithmeticExpression"})
public class P2PPacker
{
    public static int byteArrayToInt(byte[] b, int i, boolean z)
    {
        return z ? byteArrayToInt_Big(b, i) : byteArrayToInt_Little(b, i);
    }

    private static int byteArrayToInt_Big(byte[] b, int i)
    {
        return ((((b[i] & 255) << 24) | ((b[i + 1] & 255) << 16)) | ((b[i + 2] & 255) << 8)) | (b[i + 3] & 255);
    }

    private static int byteArrayToInt_Little(byte[] b, int i)
    {
        return (((b[i] & 255) | ((b[i + 1] & 255) << 8)) | ((b[i + 2] & 255) << 16)) | ((b[i + 3] & 255) << 24);
    }

    public static long byteArrayToLong(byte[] b, int i, boolean z)
    {
        return z ? byteArrayToLong_Big(b, i) : byteArrayToLong_Little(b, i);
    }

    private static long byteArrayToLong_Big(byte[] b, int i)
    {
        return 0
                | (((long) b[i + 0]) << 56)
                | (((long) b[i + 1]) << 48)
                | (((long) b[i + 2]) << 40)
                | (((long) b[i + 3]) << 32)
                | (((long) b[i + 4]) << 24)
                | (((long) b[i + 5]) << 16)
                | (((long) b[i + 6]) << 8)
                | (((long) b[i + 7]) << 0)
                ;
    }

    private static long byteArrayToLong_Little(byte[] b, int i)
    {
        return 0
                | (((long) b[i + 0] & 255) << 0)
                | (((long) b[i + 1] & 255) << 8)
                | (((long) b[i + 2] & 255) << 16)
                | (((long) b[i + 3] & 255) << 24)
                | (((long) b[i + 4] & 255) << 32)
                | (((long) b[i + 5] & 255) << 40)
                | (((long) b[i + 6] & 255) << 48)
                | (((long) b[i + 7] & 255) << 56)
                ;
    }

    public static short byteArrayToShort(byte[] b, int i, boolean z)
    {
        return z ? byteArrayToShort_Big(b, i) : byteArrayToShort_Little(b, i);
    }

    private static short byteArrayToShort_Big(byte[] b, int i)
    {
        return (short) (((b[i] & 255) << 8) | (b[i + 1] & 255));
    }

    private static short byteArrayToShort_Little(byte[] b, int i)
    {
        return (short) ((b[i] & 255) | ((b[i + 1] & 255) << 8));
    }

    public static String byteArrayToString(byte[] b, int i)
    {
        return new String(b);
    }

    public static byte[] intToByteArray(int i, boolean z)
    {
        return z ? intToByteArray_Big(i) : intToByteArray_Little(i);
    }

    private static byte[] intToByteArray_Big(int i)
    {
        return new byte[]
                {
                        (byte) (i >>> 24),
                        (byte) (i >>> 16),
                        (byte) (i >>> 8),
                        (byte) (i >>> 0)
                };
    }

    private static byte[] intToByteArray_Little(int i)
    {
        return new byte[]
                {
                        (byte) (i >>> 0),
                        (byte) (i >>> 8),
                        (byte) (i >>> 16),
                        (byte) (i >>> 24)
                };
    }

    public static byte[] longToByteArray(long j, boolean z)
    {
        return z ? longToByteArray_Big(j) : longToByteArray_Little(j);
    }

    private static byte[] longToByteArray_Big(long j)
    {
        return new byte[]
                {
                        (byte) ((int) (j >>> 56)),
                        (byte) ((int) (j >>> 48)),
                        (byte) ((int) (j >>> 40)),
                        (byte) ((int) (j >>> 32)),
                        (byte) ((int) (j >>> 24)),
                        (byte) ((int) (j >>> 16)),
                        (byte) ((int) (j >>> 8)),
                        (byte) ((int) (j >>> 0))
                };
    }

    private static byte[] longToByteArray_Little(long j)
    {
        return new byte[]
                {
                        (byte) ((int) (j >>> 0)),
                        (byte) ((int) (j >>> 8)),
                        (byte) ((int) (j >>> 16)),
                        (byte) ((int) (j >>> 24)),
                        (byte) ((int) (j >>> 32)),
                        (byte) ((int) (j >>> 40)),
                        (byte) ((int) (j >>> 48)),
                        (byte) ((int) (j >>> 56))
                };
    }

    public static String printByteArray(byte[] b, int i)
    {
        return new String(b, 0, i);
    }

    public static byte[] shortToByteArray(short s, boolean z)
    {
        return z ? shortToByteArray_Big(s) : shortToByteArray_Little(s);
    }

    private static byte[] shortToByteArray_Big(short s)
    {
        return new byte[]{(byte) (s >>> 8), (byte) s};
    }

    private static byte[] shortToByteArray_Little(short s)
    {
        return new byte[]{(byte) s, (byte) (s >>> 8)};
    }
}
