package de.xavaro.android.yihome;

public class P2PPacker
{
    public static int byteArrayToInt(byte[] bArr, int i, boolean z)
    {
        return z ? byteArrayToInt_Big(bArr, i) : byteArrayToInt_Little(bArr, i);
    }

    private static int byteArrayToInt_Big(byte[] bArr, int i)
    {
        return ((((bArr[i] & 255) << 24) | ((bArr[i + 1] & 255) << 16)) | ((bArr[i + 2] & 255) << 8)) | (bArr[i + 3] & 255);
    }

    private static int byteArrayToInt_Little(byte[] bArr, int i)
    {
        return (((bArr[i] & 255) | ((bArr[i + 1] & 255) << 8)) | ((bArr[i + 2] & 255) << 16)) | ((bArr[i + 3] & 255) << 24);
    }

    public static long byteArrayToLong(byte[] bArr, int i, boolean z)
    {
        return z ? byteArrayToLong_Big(bArr, i) : byteArrayToLong_Little(bArr, i);
    }

    private static long byteArrayToLong_Big(byte[] bArr, int i)
    {
        return (long) (((((((((bArr[i] & 255) << 56) | ((bArr[i + 1] & 255) << 48)) | ((bArr[i + 2] & 255) << 40)) | ((bArr[i + 3] & 255) << 32)) | ((bArr[i + 4] & 255) << 24)) | ((bArr[i + 5] & 255) << 16)) | ((bArr[i + 6] & 255) << 8)) | (bArr[i + 7] & 255));
    }

    private static long byteArrayToLong_Little(byte[] bArr, int i)
    {
        return (long) ((((((((bArr[i] & 255) | ((bArr[i + 1] & 255) << 8)) | ((bArr[i + 2] & 255) << 16)) | ((bArr[i + 3] & 255) << 24)) | ((bArr[i + 4] & 255) << 32)) | ((bArr[i + 5] & 255) << 40)) | ((bArr[i + 6] & 255) << 48)) | ((bArr[i + 7] & 255) << 56));
    }

    public static short byteArrayToShort(byte[] bArr, int i, boolean z)
    {
        return z ? byteArrayToShort_Big(bArr, i) : byteArrayToShort_Little(bArr, i);
    }

    private static short byteArrayToShort_Big(byte[] bArr, int i)
    {
        return (short) (((bArr[i] & 255) << 8) | (bArr[i + 1] & 255));
    }

    private static short byteArrayToShort_Little(byte[] bArr, int i)
    {
        return (short) ((bArr[i] & 255) | ((bArr[i + 1] & 255) << 8));
    }

    public static String byteArrayToString(byte[] bArr, int i)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++)
        {
            stringBuilder.append((char) bArr[i2]);
        }
        return stringBuilder.toString();
    }

    public static byte[] intToByteArray(int i, boolean z)
    {
        return z ? intToByteArray_Big(i) : intToByteArray_Little(i);
    }

    private static byte[] intToByteArray_Big(int i)
    {
        return new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i};
    }

    private static byte[] intToByteArray_Little(int i)
    {
        return new byte[]{(byte) i, (byte) (i >>> 8), (byte) (i >>> 16), (byte) (i >>> 24)};
    }

    public static byte[] longToByteArray(long j, boolean z)
    {
        return z ? longToByteArray_Big(j) : longToByteArray_Little(j);
    }

    private static byte[] longToByteArray_Big(long j)
    {
        return new byte[]{(byte) ((int) (j >>> 56)), (byte) ((int) (j >>> 48)), (byte) ((int) (j >>> 40)), (byte) ((int) (j >>> 32)), (byte) ((int) (j >>> 24)), (byte) ((int) (j >>> 16)), (byte) ((int) (j >>> 8)), (byte) ((int) j)};
    }

    private static byte[] longToByteArray_Little(long j)
    {
        return new byte[]{(byte) ((int) j), (byte) ((int) (j >>> 8)), (byte) ((int) (j >>> 16)), (byte) ((int) (j >>> 24)), (byte) ((int) (j >>> 32)), (byte) ((int) (j >>> 40)), (byte) ((int) (j >>> 48)), (byte) ((int) (j >>> 56))};
    }

    public static String printByteArray(byte[] bArr, int i)
    {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i2 = 0; i2 < i; i2++)
        {
            stringBuilder.append(" " + bArr[i2]);
        }
        return stringBuilder.toString();
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
