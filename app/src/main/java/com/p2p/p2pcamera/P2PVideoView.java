package com.p2p.p2pcamera;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class P2PVideoView extends GLSurfaceView
{
    private static final String LOGTAG = P2PVideoView.class.getSimpleName();

    private P2PVideoFrameRenderer renderer;

    public P2PVideoView(Context context)
    {
        super(context);
        init(context);
    }

    public P2PVideoView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context)
    {
        if (supportsOpenGLES2(context))
        {
            renderer = new P2PVideoFrameRenderer();

            setEGLContextClientVersion(2);
            setEGLConfigChooser(new P2PVideoConfigChooser.ComponentSizeChooser(8, 8, 8, 8, 0, 0));
            getHolder().setFormat(1);
            setRenderer(renderer);
            setRenderMode(0);
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

    public P2PVideoGLImage getRGBImage()
    {
        return renderer.getRGBImage();
    }

    public int[] getYUVTextures()
    {
        return renderer.getYUVTextures();
    }
}
