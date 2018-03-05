package zz.top.p2p.video;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

import zz.top.p2p.camera.P2PAVFrame;

public class VideoGLSurfaceView extends GLSurfaceView
{
    private static final String LOGTAG = VideoGLSurfaceView.class.getSimpleName();

    public VideoGLRenderer renderer;

    public VideoGLSurfaceView(Context context)
    {
        super(context);
        init(context);
    }

    public VideoGLSurfaceView(Context context, AttributeSet attributeSet)
    {
        super(context, attributeSet);
        init(context);
    }

    private void init(Context context)
    {
        if (supportsOpenGLES2(context))
        {
            renderer = new VideoGLRenderer(context);

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

    public void renderFrame(P2PAVFrame avFrame)
    {
        renderer.renderFrame(avFrame);
    }
}
