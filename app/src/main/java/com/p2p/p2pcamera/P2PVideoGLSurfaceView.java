package com.p2p.p2pcamera;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class P2PVideoGLSurfaceView extends GLSurfaceView
{
    private static final String LOGTAG = P2PVideoGLSurfaceView.class.getSimpleName();

    private P2PVideoGLRenderer renderer;

    public P2PVideoGLSurfaceView(Context context)
    {
        super(context);
        init(context);
    }

    public P2PVideoGLSurfaceView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context)
    {
        if (supportsOpenGLES2(context))
        {
            renderer = new P2PVideoGLRenderer();

            setEGLContextClientVersion(2);

            setRenderer(renderer);
            setRenderMode(RENDERMODE_WHEN_DIRTY);

            getHolder().setFormat(PixelFormat.RGBA_8888);
        }
        else
        {
            throw new RuntimeException("Device does not support gles 2.0!");
        }
    }

    private boolean supportsOpenGLES2(Context context)
    {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        return (am != null) && (am.getDeviceConfigurationInfo().reqGlEsVersion >= 0x20000);
    }

    public void setSourceDecoder(P2PVideoGLDecoder decoder)
    {
        renderer.setSourceDecoder(decoder);
    }

    public void setSourceDimensions(int width, int height)
    {
        renderer.setSourceDimensions(width, height);
    }
}
