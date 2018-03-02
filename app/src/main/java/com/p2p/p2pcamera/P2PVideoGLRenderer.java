package com.p2p.p2pcamera;

import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class P2PVideoGLRenderer implements GLSurfaceView.Renderer
{
    private final String LOGTAG = P2PVideoGLRenderer.class.getSimpleName();

    private P2PVideoGLShaderYUV2RGB yuvShader;
    private P2PVideoGLShaderRGB2SUR rgbShader;
    private P2PVideoGLImage rgbImage;

    private P2PVideoGLDecoder decoder;

    private int sourceWidth;
    private int sourceHeight;

    private int displayWidth;
    private int displayHeight;

    private int modcount;

    public void setSourceDecoder(P2PVideoGLDecoder decoder)
    {
        this.decoder = decoder;
    }

    public void setSourceDimensions(int width, int height)
    {
        sourceWidth = width;
        sourceHeight = height;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig eGLConfig)
    {
        Log.d(LOGTAG, "onSurfaceCreated.");

        rgbImage = new P2PVideoGLImage();

        yuvShader = new P2PVideoGLShaderYUV2RGB();
        rgbShader = new P2PVideoGLShaderRGB2SUR();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        Log.d(LOGTAG, "onSurfaceChanged.");

        displayWidth = width;
        displayHeight = height;
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        boolean ok;

        synchronized (P2PLocks.decoderLock)
        {
            int[] yuvTextures = yuvShader.getYUVTextures();

            ok = (decoder != null) && (decoder.toTextureDecoder(yuvTextures[0], yuvTextures[1], yuvTextures[2]) >= 0);
        }

        if (ok) ok = yuvShader.process(rgbImage, sourceWidth, sourceHeight);

        //setRenderMatrix(image.width(), image.height());

        if (ok) ok = rgbShader.process(rgbImage, 320, 180);

        if ((modcount++ % 30) == 0) Log.d(LOGTAG, "onDrawFrame ok=" + ok);
    }
}