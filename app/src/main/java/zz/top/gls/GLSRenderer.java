package zz.top.gls;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.SparseArray;
import android.util.Log;

import com.google.android.gms.vision.face.Face;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import zz.top.dec.VIDDecode;

public class GLSRenderer implements GLSurfaceView.Renderer
{
    private final String LOGTAG = GLSRenderer.class.getSimpleName();

    private GLSShaderYUV2RGB yuvShader;
    private GLSShaderRGB2SUR rgbShader;
    private GLSImage rgbImage;

    private GLSDecoder decoder;

    private int sourceCodec;
    private int sourceWidth;
    private int sourceHeight;

    private int displayWidth;
    private int displayHeight;

    private GLSFaceDetect faceDetector;

    private int lastframes;
    private long lasttimems;

    public final ArrayList<GLSFrame> decodeFrames = new ArrayList<>();

    public GLSRenderer(Context context)
    {
        super();

        faceDetector = new GLSFaceDetect(context);
    }

    public void renderFrame(GLSFrame avFrame)
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

        rgbImage = new GLSImage();

        yuvShader = new GLSShaderYUV2RGB();
        rgbShader = new GLSShaderRGB2SUR();
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
            GLSFrame avFrame = null;

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
                if (decoder != null)
                {
                    Log.d(LOGTAG, "onDrawFrame: releaseDecoder codec=" + sourceCodec);

                    decoder.releaseDecoder();
                    decoder = null;
                }

                sourceCodec = avFrame.getCodecId();
                decoder = new VIDDecode(sourceCodec);

                Log.d(LOGTAG, "onDrawFrame: createDecoder codec=" + avFrame.getCodecId());
            }

            sourceWidth = avFrame.getVideoWidth();
            sourceHeight = avFrame.getVideoHeight();

            display = decoder.decodeDecoder(avFrame.getFrameData(), avFrame.getFrameSize(), avFrame.getTimeStamp());
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