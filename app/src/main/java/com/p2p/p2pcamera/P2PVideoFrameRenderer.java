package com.p2p.p2pcamera;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.decoder.xiaomi.DecoderBase;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class P2PVideoFrameRenderer implements GLSurfaceView.Renderer
{
    private final String LOGTAG = P2PVideoFrameRenderer.class.getSimpleName();

    private P2PVideoShaderYUV2RGB yuvShader;
    private P2PVideoShaderRGB2SUR rgbShader;
    private P2PVideoGLImage rgbImage;

    private DecoderBase decoder;

    private int sourceWidth;
    private int sourceHeight;

    private int displayWidth;
    private int displayHeight;

    private int modcount;

    public void setSourceDecoder(DecoderBase decoder)
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

        yuvShader = new P2PVideoShaderYUV2RGB();
        rgbShader = new P2PVideoShaderRGB2SUR();
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