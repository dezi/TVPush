package com.p2p.p2pcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.renderscript.Matrix4f;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.face.Face;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.xavaro.android.common.Faces;
import de.xavaro.android.tvpush.ApplicationBase;
import de.xavaro.android.tvpush.MainActivity;

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
    private Faces faceDetector;

    public P2PVideoGLRenderer(Context context)
    {
        super();

        faceDetector = new Faces(context);
    }

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

        if (ok)
        {
            if ((onFacesDetectedListener != null) && (faceDetector != null))
            {
                onFacesDetectedListener.onFacesDetected(faceDetector.detect(rgbImage.save()));
            }
        }

        if ((modcount % 30) == 0)
        {
            if ((sourceWidth > 0) && (sourceHeight > 0))
            {
                MainActivity.rgbBitmap = rgbImage.save();
                ApplicationBase.handler.post(MainActivity.updateRGB);
            }
        }

        if (ok) ok = rgbShader.process(rgbImage, displayWidth, displayHeight);

        if ((modcount++ % 30) == 0)
        {
            Log.d(LOGTAG, "onDrawFrame ok=" + ok + " width=" + sourceWidth + " height=" + sourceHeight);
        }
    }

    //region OnFacesDetectedListener

    private OnFacesDetectedListener onFacesDetectedListener;

    public void setOnFacesDetectedListener(OnFacesDetectedListener listener)
    {
        onFacesDetectedListener = listener;
    }

    public OnFacesDetectedListener getOnFacesDetectedListener()
    {
        return onFacesDetectedListener;
    }

    public void OnFacesDetected(SparseArray<Face> faces)
    {
        Log.d(LOGTAG, "OnFacesDetected:"
        );

        if (onFacesDetectedListener != null)
        {
            onFacesDetectedListener.onFacesDetected(faces);
        }
    }

    public interface OnFacesDetectedListener
    {
        void onFacesDetected(SparseArray<Face> faces);
    }

    //endregion OnFacesDetectedListener
}