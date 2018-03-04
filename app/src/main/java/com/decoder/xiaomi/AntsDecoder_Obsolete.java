package com.decoder.xiaomi;

import java.nio.ByteBuffer;

import com.p2p.p2pcamera.P2PVideoGLDecoder;

public class AntsDecoder_Obsolete extends P2PVideoGLDecoder
{
    protected int mNativeContext;

    static
    {
        nativeInit();
    }

    private static native void nativeInit();

    public AntsDecoder_Obsolete(int i)
    {
        initDecoder(i);
    }


    private native void init(int i);

    public native boolean decode(byte[] bArr, int i, long j);

    public native boolean decodeBuffer(ByteBuffer byteBuffer, int i, long j);

    public boolean decodeBufferDecoder(ByteBuffer byteBuffer, int i, long j)
    {
        return decodeBuffer(byteBuffer, i, j);
    }

    public boolean decodeDecoder(byte[] bArr, int i, long j)
    {
        return decode(bArr, i, j);
    }

    public native int getHeight();

    public int getHeightDecoder()
    {
        return getHeight();
    }

    public native int getWidth();

    public int getWidthDecoder()
    {
        return getWidth();
    }

    public void initDecoder(int i)
    {
        init(i);
    }

    public native void release();

    public void releaseDecoder()
    {
        release();
    }

    public native int toTexture(int i, int i2, int i3);

    public int toTextureDecoder(int i, int i2, int i3)
    {
        return toTexture(i, i2, i3);
    }
}
