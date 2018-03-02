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
    private P2PVideoShaderFrames frameShader;
    private P2PVideoGLImage rgbImage;

    private DecoderBase decoder;

    private int sourceWidth;
    private int sourceHeight;

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
    public void onSurfaceCreated(GL10 unused, EGLConfig eGLConfig)
    {
        Log.d(LOGTAG, "onSurfaceCreated.");

        rgbImage = new P2PVideoGLImage();
        yuvShader = new P2PVideoShaderYUV2RGB();
        frameShader = new P2PVideoShaderFrames();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height)
    {
        Log.d(LOGTAG, "onSurfaceChanged.");
    }

    @Override
    public void onDrawFrame(GL10 unused)
    {
        if ((modcount++ % 30) == 0) Log.d(LOGTAG, "onDrawFrame.");

        boolean ok = false;

        if ((yuvShader != null) && (frameShader != null) && (rgbImage != null))
        {
            int[] yuvTextures = yuvShader.getYUVTextures();

            synchronized (P2PLocks.decoderLock)
            {
                ok = (decoder != null) && (decoder.toTextureDecoder(yuvTextures[0], yuvTextures[1], yuvTextures[2]) >= 0);
            }

            if (ok) ok = yuvShader.process(rgbImage, sourceWidth, sourceHeight);

            //setRenderMatrix(image.width(), image.height());

            if (ok) frameShader.process(rgbImage, 320, 180);
        }
    }
}