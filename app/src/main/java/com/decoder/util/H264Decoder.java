package com.decoder.util;

@SuppressWarnings("JniMissingFunction")
public class H264Decoder
{
    public static final int COLOR_FORMAT_YUV420 = 0;
    public static final int COLOR_FORMAT_RGB565LE = 1;
    public static final int COLOR_FORMAT_BGR32 = 2;

    private int cdata;

    public H264Decoder(int i)
    {
        nativeInit(i);
    }

    private native void nativeInit(int i);
    public native void nativeDestroy();

    public native int consumeNalUnitsFromDirectBuffer(byte[] bArr, int i, long j);

    public native int getWidth();
    public native int getHeight();

    public native int getOutputByteSize();

    public native long getYUVData(byte[] bArr, int i);

    public native boolean isFrameReady();

    public native long yuv2argb(byte[] bArr, int i, int i2, int i3);
}
