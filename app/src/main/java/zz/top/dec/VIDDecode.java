package zz.top.dec;

import java.nio.ByteBuffer;

import zz.top.gls.GLSDecoder;

public class VIDDecode extends GLSDecoder
{
    protected int mNativeContext;

    static
    {
        nativeInit();
    }

    public VIDDecode(int i)
    {
        initDecoder(i);
    }

    public boolean decodeBufferDecoder(ByteBuffer byteBuffer, int i, long j)
    {
        return decodeBuffer(byteBuffer, i, j);
    }

    public boolean decodeDecoder(byte[] bArr, int i, long j)
    {
        return decode(bArr, i, j);
    }

    public int getHeightDecoder()

    {
        return getHeight();
    }

    public int getWidthDecoder()
    {
        return getWidth();
    }

    public void initDecoder(int i)
    {
        init(i);
    }

    public void releaseDecoder()
    {
        release();
    }

    public int toTextureDecoder(int i, int i2, int i3)
    {
        return toTexture(i, i2, i3);
    }

    public static native void nativeInit();

    public native void init(int i);
    public native boolean decode(byte[] bArr, int i, long j);
    public native boolean decodeBuffer(ByteBuffer byteBuffer, int i, long j);
    public native int getHeight();
    public native int getWidth();
    public native void release();
    public native int toTexture(int i, int i2, int i3);
}
