package com.aac.utils;

@SuppressWarnings("JniMissingFunction")
public class DecodeAAC
{
    public static native int nOpen();

    public static native int nDecode(byte[] bArr, int i, byte[] bArr2, int i2);

    public static native int nClose();

}
