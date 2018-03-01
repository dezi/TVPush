package com.p2p.p2pcamera;

import android.app.ActivityManager;
import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class P2PVideoView extends GLSurfaceView
{
    private static final String LOGTAG = P2PVideoView.class.getSimpleName();

    private FrameRenderer renderer;
    private P2PVideoRenderUtils.RenderContext renderContext;

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
            renderer = new FrameRenderer();
            setEGLContextClientVersion(2);
            setEGLConfigChooser(new P2PVideoConfigChooser.ComponentSizeChooser(8, 8, 8, 8, 0, 0));
            getHolder().setFormat(1);
            setRenderer(renderer);
            setRenderMode(0);

            return;
        }

        throw new RuntimeException("Device does not support gles 2.0!");
    }

    private boolean supportsOpenGLES2(Context context)
    {
        ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        return (am != null) && (am.getDeviceConfigurationInfo().reqGlEsVersion >= 131072);
    }

    private P2PVideoStillImage image;

    public void setStillImage(P2PVideoStillImage image)
    {
        this.image = image;
    }

    class FrameRenderer implements Renderer
    {
        private final String LOGTAG = P2PVideoView.class.getSimpleName();

        public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig)
        {
            Log.d(LOGTAG, "onSurfaceCreated.");

            renderContext = P2PVideoRenderUtils.createProgram();
        }

        public void onSurfaceChanged(GL10 gl10, int i, int i2)
        {
            Log.d(LOGTAG, "onSurfaceChanged.");
        }

        public void onDrawFrame(GL10 gl10)
        {
            Log.d(LOGTAG, "onDrawFrame.");

            if (image != null)
            {
                //setRenderMatrix(image.width(), image.height());

                P2PVideoRenderUtils.renderTexture(renderContext, image.texture(), image.width(), image.height());
            }
        }
    }
}
