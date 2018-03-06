package zz.top.gls;

import android.content.Context;
import android.content.res.Resources;
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

    private GLSShaderRGB2SUR rgbShader;
    private GLSShaderYUV2RGB yuvShader;

    private int sourceCodec;
    private int sourceWidth;
    private int sourceHeight;

    private int displayWidth;
    private int displayHeight;

    private GLSImage screenShot;
    private GLSDecoder videoDecoder;
    private GLSFaceDetect faceDetector;

    private int framesDecoded;
    private int framesCorrupt;

    private int lastframes;
    private long lasttimems;

    public final ArrayList<GLSFrame> frameQueue = new ArrayList<>();

    public GLSRenderer(Context context)
    {
        super();

        faceDetector = new GLSFaceDetect(context);
    }

    public void renderFrame(GLSFrame avFrame)
    {
        synchronized (frameQueue)
        {
            frameQueue.add(avFrame);
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig eGLConfig)
    {
        Log.d(LOGTAG, "onSurfaceCreated.");

        yuvShader = new GLSShaderYUV2RGB();
        rgbShader = new GLSShaderRGB2SUR();

        screenShot = new GLSImage();
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
        //
        // Display the last valid image.
        //

        if ((screenShot.getWidth() > 0) && (screenShot.getWidth() > 0))
        {
            rgbShader.process(screenShot, displayWidth, displayHeight);
        }

        //
        // Decode new frames.
        //

        boolean display = false;

        while (frameQueue.size() > 0)
        {
            GLSFrame avFrame = null;

            synchronized (frameQueue)
            {
                if (frameQueue.size() > 0)
                {
                    avFrame = frameQueue.remove(0);
                }
            }

            if (avFrame == null) break;

            //
            // Check decoder and decoding sizes.
            //

            if ((videoDecoder == null)
                    || (sourceCodec != avFrame.getCodecId())
                    || (sourceWidth != avFrame.getVideoWidth())
                    || (sourceHeight != avFrame.getVideoHeight()))
            {
                if (videoDecoder != null)
                {
                    Log.d(LOGTAG, "renderFrame: releaseDecoder codec=" + sourceCodec);

                    videoDecoder.releaseDecoder();
                    videoDecoder = null;
                }

                sourceCodec = avFrame.getCodecId();
                sourceWidth = avFrame.getVideoWidth();
                sourceHeight = avFrame.getVideoHeight();

                videoDecoder = new VIDDecode(sourceCodec);

                framesDecoded = 0;
                framesCorrupt = 0;

                Log.d(LOGTAG, "renderFrame: createDecoder codec=" + avFrame.getCodecId());
            }

            if (videoDecoder.decodeDecoder(avFrame.getFrameData(), avFrame.getFrameSize(), 0))
            {
                framesDecoded++;

                display = true;
            }
            else
            {
                framesCorrupt++;
            }
        }

        if (display)
        {
            int[] yuvTextures = yuvShader.getYUVTextures();

            if (videoDecoder.toTextureDecoder(yuvTextures[0], yuvTextures[1], yuvTextures[2]) >= 0)
            {
                yuvShader.process(screenShot, sourceWidth, sourceHeight);

                lastframes++;
            }

            //
            // Update FPS statistics,
            //

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
                            + " dec=" + framesDecoded
                            + " bad=" + framesCorrupt
                    );

                    lastframes = 0;
                    lasttimems = System.currentTimeMillis();
                }
            }

            if ((onFacesDetectedListener != null) && (faceDetector != null))
            {
                onFacesDetectedListener.onFacesDetected(
                        faceDetector.detect(screenShot.save()),
                        screenShot.getWidth(),
                        screenShot.getHeight());
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