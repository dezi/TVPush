package zz.top.gls;

import android.opengl.GLSurfaceView;
import android.util.SparseArray;
import android.content.Context;
import android.graphics.Bitmap;

import com.google.android.gms.vision.face.Face;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import pub.android.interfaces.pub.PUBSurface;
import zz.top.dec.VIDDecode;
import zz.top.utl.Log;

public class GLSRenderer implements GLSurfaceView.Renderer, PUBSurface
{
    private final static String LOGTAG = GLSRenderer.class.getSimpleName();

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
    private Thread faceDetectWorker;

    private int[] yuvTextures;

    private Buffer faceBuffer;
    private Bitmap faceBitmap;

    private int framesDecoded;
    private int framesCorrupt;

    private int lastframes;
    private long lasttimems;

    private final ArrayList<Buffer> facesQueue = new ArrayList<>();
    private final ArrayList<GLSFrame> frameQueue = new ArrayList<>();
    private final ArrayList<GLSFrame> renderQueue = new ArrayList<>();

    private int zoom;
    private int step;

    public GLSRenderer(Context context)
    {
        super();

        faceDetector = new GLSFaceDetect(context);
    }

    @Override
    public void setZoom(int zoom, int step)
    {
        this.zoom = zoom;
        this.step = step;

        Log.d(LOGTAG, "setZoom: zoom=" + zoom + " step=" + step);

        yuvShader.setZoom(zoom, step, displayWidth, displayHeight);
    }

    private void startFaceDetect()
    {
        if (faceDetectWorker == null)
        {
            faceDetectWorker = new Thread(faceDetectWorkerRunner);
            faceDetectWorker.setPriority(Thread.MIN_PRIORITY);
            faceDetectWorker.start();
        }
    }

    private void stopFaceDetect()
    {
        if (faceDetectWorker != null)
        {
            faceDetectWorker.interrupt();
            faceDetectWorker = null;
        }
    }

    private final Runnable faceDetectWorkerRunner = new Runnable()
    {
        @Override
        public void run()
        {
            while (faceDetectWorker != null)
            {
                try
                {
                    Thread.sleep(20);
                }
                catch (Exception ignore)
                {
                }

                if ((sourceWidth == 0) || (sourceHeight == 0))
                {
                    //
                    // Decoder not yet ready.
                    //

                    continue;
                }

                //
                // Create bitmap outside synchronized region.
                //

                if ((faceBitmap == null)
                        || (faceBitmap.getWidth() != sourceWidth)
                        || (faceBitmap.getHeight() != sourceHeight))
                {
                    if (faceBitmap != null) faceBitmap.recycle();

                    faceBitmap = Bitmap.createBitmap(sourceWidth, sourceHeight, Bitmap.Config.ARGB_8888);
                }

                boolean work = false;

                synchronized (facesQueue)
                {
                    if (facesQueue.size() > 0)
                    {
                        //
                        // Check in synchronize region of bitmap still
                        // fits. Otherwise skip this bitmap.
                        //

                        if ((faceBitmap.getWidth() == sourceWidth) &&
                                (faceBitmap.getHeight() == sourceHeight))
                        {
                            faceBuffer.rewind();
                            faceBitmap.copyPixelsFromBuffer(faceBuffer);

                            work = true;
                        }
                    }
                }

                if (work)
                {
                    SparseArray<Face> faces = faceDetector.detect(faceBitmap);

                    onFacesDetectedListener.onFacesDetected(faces, faceBitmap.getWidth(), faceBitmap.getHeight());

                    Log.d(LOGTAG, "faceDetectWorkerRunner:"
                            + " width=" + sourceWidth
                            + " height=" + sourceHeight
                            + " faces=" + faces.size());
                }

                //
                // Signal renderer that we like another frame.
                //

                facesQueue.clear();
            }
        }
    };

    public void renderFrame(GLSFrame avFrame)
    {
        synchronized (frameQueue)
        {
            if (frameQueue.size() == 0)
            {
                frameQueue.add(avFrame);

                return;
            }

            int frst = frameQueue.get(0).getFrameNo();;
            int last = frameQueue.get(frameQueue.size() - 1).getFrameNo();
            int curr = avFrame.getFrameNo();

            //
            // Fuck this. I-Frames take longer to
            // compress and therefore come out of order.
            // They are usually 4 - 5 frames late.
            // All frames must be inserted in the
            // right slot in the list.
            //

            int index;

            for (index = 0; index < frameQueue.size(); index++)
            {
                GLSFrame frame = frameQueue.get(index);

                if (frame.getFrameNo() > curr)
                {
                    break;
                }
            }

            frameQueue.add(index, avFrame);

            synchronized (renderQueue)
            {
                while (frameQueue.size() > 1)
                {
                    if ((frameQueue.get(0).getFrameNo() + 1) == frameQueue.get(1).getFrameNo())
                    {
                        renderQueue.add(frameQueue.remove(0));
                    }
                    else
                    {
                        //
                        // A frame is missing. Reset to next I-Frame.
                        //

                        if (frameQueue.size() > 10)
                        {
                            //
                            // Reset until next I-Frame.
                            //

                            while (frameQueue.size() > 0)
                            {
                                GLSFrame frame = frameQueue.get(0);
                                if (frame.isIFrame()) break;
                                frameQueue.remove(0);
                            }
                        }

                        break;
                    }
                }
            }

            if (avFrame.isIFrame())
            {
                if (curr > (last + 2))
                {
                    Log.e(LOGTAG, "renderFrame: iframe"
                            + " curr=" + curr
                            + " frst=" + frst
                            + " last=" + last
                    );
                }
            }
        }
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig eGLConfig)
    {
        Log.d(LOGTAG, "onSurfaceCreated.");

        screenShot = new GLSImage();

        yuvShader = new GLSShaderYUV2RGB();
        rgbShader = new GLSShaderRGB2SUR();

        yuvTextures = yuvShader.getYUVTextures();
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

        if (framesDecoded > 0)
        {
            yuvShader.process(null, displayWidth, displayHeight);
        }

        //
        // Decode new frames.
        //

        boolean display = false;

        GLSFrame avFrame = null;

        while (renderQueue.size() > 0)
        {
            synchronized (renderQueue)
            {
                avFrame = renderQueue.remove(0);
            }

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

                synchronized (facesQueue)
                {
                    sourceCodec = avFrame.getCodecId();
                    sourceWidth = avFrame.getVideoWidth();
                    sourceHeight = avFrame.getVideoHeight();

                    faceBuffer = ByteBuffer.allocate(sourceWidth * sourceHeight * 4);
                }

                videoDecoder = new VIDDecode(sourceCodec);

                framesDecoded = 0;
                framesCorrupt = 0;

                Log.d(LOGTAG, "renderFrame: createDecoder codec=" + avFrame.getCodecId());

                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            }

            if (videoDecoder.decodeDecoder(avFrame.getFrameData(), avFrame.getFrameSize(), avFrame.getTimeStamp()))
            {
                display = true;
                framesDecoded++;
            }
            else
            {
                framesCorrupt++;
            }
        }

        if (! display)
        {
            //
            // We are at end of queue or frames are corrupt.
            //

            return;
        }

        lastframes++;

        if (videoDecoder.toTextureDecoder(yuvTextures[0], yuvTextures[1], yuvTextures[2]) < 0)
        {
            //
            // Happens never. Hopefully.
            //

            return;
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
                int back = renderQueue.size() + frameQueue.size();

                Log.d(LOGTAG, "onDrawFrame:"
                        + " fps=" + lastframes
                        + " back=" + back
                        + " width=" + sourceWidth
                        + " height=" + sourceHeight
                        + " fno=" + avFrame.getFrameNo()
                        + " dec=" + framesDecoded
                        + " bad=" + framesCorrupt
                );

                lastframes = 0;
                lasttimems = System.currentTimeMillis();
            }
        }

        if ((onFacesDetectedListener != null) && (faceDetector != null))
        {
            if (facesQueue.size() == 0)
            {
                //
                // Process current YUV into texture and save raw pixels in buffer.
                //

                synchronized (facesQueue)
                {
                    yuvShader.process(screenShot, sourceWidth, sourceHeight);

                    screenShot.save(faceBuffer);
                    facesQueue.add(faceBuffer);
                }
            }
        }
    }

    //region OnFacesDetectedListener

    private OnFacesDetectedListener onFacesDetectedListener;

    public void setOnFacesDetectedListener(OnFacesDetectedListener listener)
    {
        onFacesDetectedListener = listener;

        if (listener != null)
        {
            startFaceDetect();
        }
        else
        {
            stopFaceDetect();
        }
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