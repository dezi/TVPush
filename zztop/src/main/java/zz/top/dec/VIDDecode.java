package zz.top.dec;

import android.content.res.Resources;
import android.util.Log;

import java.nio.ByteBuffer;

import zz.top.cam.Camera;
import zz.top.gls.GLSDecoder;

public class VIDDecode extends GLSDecoder
{
    private static final String LOGTAG = VIDDecode.class.getSimpleName();

    protected int mNativeContext;

    static
    {
        Log.d(LOGTAG, " static: nativeInit() start...");

        nativeInit();

        Log.d(LOGTAG, " static: nativeInit() done...");
    }

    public VIDDecode(int i)
    {
        initDecoder(i);
    }

    public void initDecoder(int i)
    {
        init(i);
    }

    public void releaseDecoder()
    {
        release();
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

    public int toTextureDecoder(int i, int i2, int i3)
    {
        return toTexture(i, i2, i3);
    }

    private static native void nativeInit();

    private native void init(int i);
    private native boolean decode(byte[] bArr, int i, long j);
    private native boolean decodeBuffer(ByteBuffer byteBuffer, int i, long j);
    private native int getHeight();
    private native int getWidth();
    private native void release();
    private native int toTexture(int i, int i2, int i3);
}
