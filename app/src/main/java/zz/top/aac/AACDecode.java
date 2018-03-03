package zz.top.aac;

public class AACDecode
{
    public static native int open();

    public static native int decode(byte[] bArr, int i, byte[] bArr2, int i2);

    public static native int close();
}
