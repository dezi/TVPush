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

    public void setYUVTextures(int[] yuvTextureIds)
    {
        if (yuvShader != null)
        {
            yuvShader.setYUVTextures(yuvTextureIds);
        }
    }

    public void setStillImage(P2PVideoGLImage image)
    {
        this.rgbImage = image;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eGLConfig)
    {
        Log.d(LOGTAG, "onSurfaceCreated.");

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

                //P2PVideoRenderUtils.renderTexture(frameShader, image.getTexture(), image.getWidth(), image.getHeight());
            }
        }
    }
}