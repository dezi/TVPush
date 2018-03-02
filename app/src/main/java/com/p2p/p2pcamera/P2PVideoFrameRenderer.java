package com.p2p.p2pcamera;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class P2PVideoFrameRenderer implements GLSurfaceView.Renderer
{
    private final String LOGTAG = P2PVideoFrameRenderer.class.getSimpleName();

    private P2PVideoShaderYUV2RGB yuvShader;
    private P2PVideoShaderFrames frameShader;

    private P2PVideoGLImage rgbImage;

    private int modcount;

    public int[] getYUVTextures()
    {
        if (yuvShader != null)
        {
            return yuvShader.getYUVTextures();
        }

        return null;
    }

    public P2PVideoGLImage getRGBImage()
    {
        return rgbImage;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig)
    {
        Log.d(LOGTAG, "onSurfaceCreated.");

        rgbImage = new P2PVideoGLImage();
        yuvShader = new P2PVideoShaderYUV2RGB();
        frameShader = new P2PVideoShaderFrames();
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height)
    {
        Log.d(LOGTAG, "onSurfaceChanged.");
    }

    @Override
    public void onDrawFrame(GL10 gl10)
    {
        if ((modcount++ % 30) == 0) Log.d(LOGTAG, "onDrawFrame.");

        if (rgbImage != null)
        {
            if (yuvShader != null)
            {
                yuvShader.process(rgbImage);
            }

            if (frameShader != null)
            {
                //setRenderMatrix(image.width(), image.height());

                frameShader.process(rgbImage, 320, 180);
            }
        }
    }
}