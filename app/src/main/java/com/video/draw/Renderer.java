package com.video.draw;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public final class Renderer implements android.opengl.GLSurfaceView.Renderer
{
    public static final int TRANSLATE_X = 0;
    public static final int TRANSLATE_Y = 1;
    public static final int TRANSLATE_Z = 2;
    private FrameListener mListener;
    private int mNativeRenderer;

    public interface FrameListener
    {
        void onNewFrame();

        void onSurfaceChanged();

        void onSurfaceCreate();
    }

    private final native void _create();

    private final native void _destroy();

    private final native void _draw();

    private final native void _init(int i, int i2);

    public void destroy()
    {
        this.mListener = null;
    }

    protected void finalize()
    {
        try
        {
            super.finalize();
        }
        catch (Throwable ignore)
        {
        }

        _destroy();
    }

    protected void fireOnNewFrame()
    {
        try
        {
            if (this.mListener != null)
            {
                this.mListener.onNewFrame();
            }
        }
        catch (Exception e)
        {
        }
    }

    public final native int getAspectState();

    public final native int getDirection();

    public final native int getFlip();

    public final native float getRotate();

    public final native float getScale();

    public final native float getTranslate(int i);

    public final native void keepAspectRatio(boolean z);

    public void onDrawFrame(GL10 gl10)
    {
        _draw();
    }

    public void onSurfaceChanged(GL10 gl10, int i, int i2)
    {
        _init(i, i2);
        if (this.mListener != null)
        {
            this.mListener.onSurfaceChanged();
        }
    }

    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig)
    {
        _create();
        if (this.mListener != null)
        {
            this.mListener.onSurfaceCreate();
        }
    }

    public final native void refresh();

    public void resetListener()
    {
        this.mListener = null;
    }

    public final native void setAspectState(int i);

    public final native void setDirection(int i);

    public final native void setFlip(int i);

    public final native void setFrame(byte[] bArr, int i, int i2);

    public void setListener(FrameListener frameListener)
    {
        this.mListener = frameListener;
    }

    public final native void setRotate(float f);

    public final native void setScale(float f);

    public final native void setTranslate(float f, float f2, float f3);
}
