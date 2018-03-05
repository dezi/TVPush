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

    private boolean hadIFrame;

    private int sourceCodec;
    private int sourceWidth;
    private int sourceHeight;

    private int displayWidth;
    private int displayHeight;

    private GLSDecoder videoDecoder;
    private GLSFaceDetect faceDetector;

    private int framesDecoded;
    private int framesCorrupt;

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

            hadIFrame = false;

            Log.d(LOGTAG, "renderFrame: createDecoder codec=" + avFrame.getCodecId());

            //
            // Clear all old frames from queue.
            //

            synchronized (decodeFrames)
            {
                decodeFrames.clear();
            }
        }

        if (hadIFrame || avFrame.isIFrame())
        {
            synchronized (decodeFrames)
            {
                decodeFrames.add(avFrame);
            }
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
        //
        // Display last correct decoded image.
        //

        if (hadIFrame)
        {
            rgbShader.process(rgbImage, displayWidth, displayHeight);
        }

        GLSFrame avFrame = null;

        synchronized (decodeFrames)
        {
            if (decodeFrames.size() > 0)
            {
                avFrame = decodeFrames.remove(0);
            }
        }

        if (avFrame == null)
        {
            //
            // Nothing to do.
            //

            return;
        }

        if (videoDecoder.decodeDecoder(avFrame.getFrameData(), avFrame.getFrameSize(), 0))
        {
            int[] yuvTextures = yuvShader.getYUVTextures();

            videoDecoder.toTextureDecoder(yuvTextures[0], yuvTextures[1], yuvTextures[2]);
            yuvShader.process(rgbImage, sourceWidth, sourceHeight);

            framesDecoded++;
            lastframes++;

            hadIFrame = true;
        }
        else
        {
            framesCorrupt++;
            hadIFrame = false;

            Log.d(LOGTAG, "onDrawFrame: frame corrupt."
                    + " iframe=" + avFrame.isIFrame()
                    + " dec=" + framesDecoded
                    + " bad=" + framesCorrupt
            );
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
                        + " back=" + decodeFrames.size()
                        + " dec=" + framesDecoded
                        + " bad=" + framesCorrupt
                );

                lastframes = 0;
                lasttimems = System.currentTimeMillis();
            }
        }

        /*
        if (display)
        {
            if ((onFacesDetectedListener != null) && (faceDetector != null))
            {
                onFacesDetectedListener.onFacesDetected(
                        faceDetector.detect(rgbImage.save()),
                        rgbImage.getWidth(),
                        rgbImage.getHeight());
            }
        }
        */
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