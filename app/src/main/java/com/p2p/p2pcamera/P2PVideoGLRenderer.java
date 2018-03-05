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

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import de.xavaro.android.common.Faces;
import de.xavaro.android.tvpush.ApplicationBase;
import de.xavaro.android.tvpush.MainActivity;
import zz.top.dec.VIDDecode;

public class P2PVideoGLRenderer implements GLSurfaceView.Renderer
{
    private final String LOGTAG = P2PVideoGLRenderer.class.getSimpleName();

    private P2PVideoGLShaderYUV2RGB yuvShader;
    private P2PVideoGLShaderRGB2SUR rgbShader;
    private P2PVideoGLImage rgbImage;

    private P2PVideoGLDecoder decoder;

    private int sourceCodec;
    private int sourceWidth;
    private int sourceHeight;

    private int displayWidth;
    private int displayHeight;

    private Faces faceDetector;

    private int lastframes;
    private long lasttimems;

    public final ArrayList<P2PAVFrame> decodeFrames = new ArrayList<>();

    public P2PVideoGLRenderer(Context context)
    {
        super();

        faceDetector = new Faces(context);
    }

    public void renderFrame(P2PAVFrame avFrame)
    {
        synchronized (decodeFrames)
        {
            decodeFrames.add(avFrame);
        }
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
        boolean display = false;

        while (decodeFrames.size() > 0)
        {
            P2PAVFrame avFrame = null;

            synchronized (decodeFrames)
            {
                avFrame = decodeFrames.remove(0);
            }

            if (lasttimems == 0)
            {
                lastframes = 0;
                lasttimems = System.currentTimeMillis();
            }
            else
            {
                long diffmillis = System.currentTimeMillis() - lasttimems;

                if (diffmillis >= 1000)
                {
                    Log.d(LOGTAG, "onDrawFrame:"
                                    + " fps=" + lastframes
                                    + " width=" + sourceWidth
                                    + " height=" + sourceHeight
                                    + " back=" + decodeFrames.size());

                    lastframes = 0;
                    lasttimems = System.currentTimeMillis();
                }
            }

            lastframes++;

            if ((decoder == null)
                    || (sourceCodec != avFrame.getCodecId())
                    || (sourceWidth != avFrame.getVideoWidth())
                    || (sourceHeight != avFrame.getVideoHeight()))
            {
                synchronized (P2PLocks.decoderLock)
                {
                    if (decoder != null)
                    {
                        Log.d(LOGTAG, "onDrawFrame: releaseDecoder codec=" + sourceCodec);

                        decoder.releaseDecoder();
                        decoder = null;
                    }

                    sourceCodec = avFrame.getCodecId();
                    decoder = new VIDDecode(sourceCodec);
                }

                Log.d(LOGTAG, "onDrawFrame: createDecoder codec=" + avFrame.getCodecId());
            }

            sourceWidth = avFrame.getVideoWidth();
            sourceHeight = avFrame.getVideoHeight();

            display = decoder.decodeDecoder(avFrame.frmData, avFrame.getFrmSize(), avFrame.getTimeStamp());
        }

        if (display)
        {
            int[] yuvTextures = yuvShader.getYUVTextures();

            decoder.toTextureDecoder(yuvTextures[0], yuvTextures[1], yuvTextures[2]);

            yuvShader.process(rgbImage, sourceWidth, sourceHeight);
            rgbShader.process(rgbImage, displayWidth, displayHeight);

            if ((onFacesDetectedListener != null) && (faceDetector != null))
            {
                onFacesDetectedListener.onFacesDetected(
                        faceDetector.detect(rgbImage.save()),
                        rgbImage.getWidth(),
                        rgbImage.getHeight());
            }
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

    public void OnFacesDetected(SparseArray<Face> faces, int imageWidth, int imageHeight)
    {
        Log.d(LOGTAG, "OnFacesDetected:");

        if (onFacesDetectedListener != null)
        {
            onFacesDetectedListener.onFacesDetected(faces, imageWidth, imageHeight);
        }
    }

    public interface OnFacesDetectedListener
    {
        void onFacesDetected(SparseArray<Face> faces, int imageWidth, int imageHeight);
    }

    //endregion OnFacesDetectedListener
}