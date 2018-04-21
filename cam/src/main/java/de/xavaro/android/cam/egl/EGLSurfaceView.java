package de.xavaro.android.cam.egl;

import java.util.concurrent.Semaphore;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

/**
 * An enhanced SurfaceView in which the camera preview will be rendered.
 * This class was needed for two reasons. <br />
 * <p>
 * First, it allows to use to feed MediaCodec with the camera preview
 * using the surface-to-buffer method while rendering it in a surface
 * visible to the user. To force the surface-to-buffer method in
 * libstreaming, call {@link MediaStream#setStreamingMethod(byte)}
 * with {@link MediaStream#MODE_MEDIACODEC_API_2}. <br />
 * <p>
 * Second, it allows to force the aspect ratio of the SurfaceView
 * to match the aspect ratio of the camera preview, so that the
 * preview do not appear distorted to the user of your app. To do
 * that, call {@link EGLSurfaceView#setAspectRatioMode(int)} with
 * {@link EGLSurfaceView#ASPECT_RATIO_PREVIEW} after creating your
 * {@link EGLSurfaceView}. <br />
 */
public class EGLSurfaceView extends android.view.SurfaceView implements Runnable, OnFrameAvailableListener, SurfaceHolder.Callback
{
    public final static String LOGTAG = EGLSurfaceView.class.getSimpleName();

    /**
     * The aspect ratio of the surface view will be equal
     * to the aspect ration of the camera preview.
     **/
    public static final int ASPECT_RATIO_PREVIEW = 0x01;

    /**
     * The surface view will fill completely fill its parent.
     */
    public static final int ASPECT_RATIO_STRETCH = 0x00;

    private Thread mThread = null;
    private Handler mHandler = null;
    private boolean mFrameAvailable = false;
    private boolean mRunning = true;
    private int mAspectRatioMode = ASPECT_RATIO_STRETCH;

    // The surface in which the preview is rendered
    private EGLSurfaceManager mViewEGLSurfaceManager = null;

    // The input surface of the MediaCodec
    private EGLSurfaceManager mCodecEGLSurfaceManager = null;

    // Handles the rendering of the SurfaceTexture we got
    // from the camera, onto a Surface
    private EGLTextureManager mEGLTextureManager = null;

    private final Semaphore mLock = new Semaphore(0);
    private final Object mSyncObject = new Object();

    // Allows to force the aspect ratio of the preview
    private ViewAspectRatioMeasurer mVARM = new ViewAspectRatioMeasurer();

    public EGLSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mHandler = new Handler();
        getHolder().addCallback(this);
    }

    public void setAspectRatioMode(int mode)
    {
        mAspectRatioMode = mode;
    }

    public SurfaceTexture getSurfaceTexture()
    {
        return mEGLTextureManager.getSurfaceTexture();
    }

    public void addMediaCodecSurface(Surface surface)
    {
        synchronized (mSyncObject)
        {
            mCodecEGLSurfaceManager = new EGLSurfaceManager(surface, mViewEGLSurfaceManager);
        }
    }

    public void removeMediaCodecSurface()
    {
        synchronized (mSyncObject)
        {
            if (mCodecEGLSurfaceManager != null)
            {
                mCodecEGLSurfaceManager.release();
                mCodecEGLSurfaceManager = null;
            }
        }
    }

    public void startGLThread()
    {
        Log.d(LOGTAG, "Thread started.");
        if (mEGLTextureManager == null)
        {
            mEGLTextureManager = new EGLTextureManager();
        }
        if (mEGLTextureManager.getSurfaceTexture() == null)
        {
            mThread = new Thread(EGLSurfaceView.this);
            mRunning = true;
            mThread.start();
            mLock.acquireUninterruptibly();
        }
    }

    @Override
    public void run()
    {
        mViewEGLSurfaceManager = new EGLSurfaceManager(getHolder().getSurface());
        mViewEGLSurfaceManager.makeCurrent();
        mEGLTextureManager.createTexture().setOnFrameAvailableListener(this);

        mLock.release();

        try
        {
            while (mRunning)
            {
                synchronized (mSyncObject)
                {
                    mSyncObject.wait(2500);

                    if (mFrameAvailable)
                    {
                        mFrameAvailable = false;

                        mViewEGLSurfaceManager.makeCurrent();
                        mEGLTextureManager.updateFrame();
                        mEGLTextureManager.drawFrame();
                        mViewEGLSurfaceManager.swapBuffer();

                        if (mCodecEGLSurfaceManager != null)
                        {
                            mCodecEGLSurfaceManager.makeCurrent();
                            mEGLTextureManager.drawFrame();
                            long ts = mEGLTextureManager.getSurfaceTexture().getTimestamp();
                            mCodecEGLSurfaceManager.setPresentationTime(ts);
                            mCodecEGLSurfaceManager.swapBuffer();
                        }
                    }
                    else
                    {
                        Log.e(LOGTAG, "No frame received !");
                    }
                }
            }
        }
        catch (InterruptedException ignore)
        {
        }
        finally
        {
            mViewEGLSurfaceManager.release();
            mEGLTextureManager.release();
        }
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture)
    {
        synchronized (mSyncObject)
        {
            mFrameAvailable = true;
            mSyncObject.notifyAll();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        if (mThread != null)
        {
            mThread.interrupt();
            mThread = null;
        }

        mRunning = false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        if ((mVARM.getAspectRatio() > 0) && (mAspectRatioMode == ASPECT_RATIO_PREVIEW))
        {
            mVARM.measure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(mVARM.getMeasuredWidth(), mVARM.getMeasuredHeight());
        }
        else
        {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }


    public void requestAspectRatio(double aspectRatio)
    {
        if (mVARM.getAspectRatio() != aspectRatio)
        {
            mVARM.setAspectRatio(aspectRatio);

            mHandler.post(new Runnable()
            {
                @Override
                public void run()
                {
                    if (mAspectRatioMode == ASPECT_RATIO_PREVIEW)
                    {
                        requestLayout();
                    }
                }
            });
        }
    }

    /**
     * This class is a helper to measure views that require a specific aspect ratio.
     *
     * @author Jesper Borgstrup
     */
    public class ViewAspectRatioMeasurer
    {
        private double aspectRatio;

        public void setAspectRatio(double aspectRatio)
        {
            this.aspectRatio = aspectRatio;
        }

        public double getAspectRatio()
        {
            return this.aspectRatio;
        }

        /**
         * Measure with the aspect ratio given at construction.<br />
         * <br />
         * After measuring, get the width and height with the {@link #getMeasuredWidth()}
         * and {@link #getMeasuredHeight()} methods, respectively.
         *
         * @param widthMeasureSpec  The width <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
         * @param heightMeasureSpec The height <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
         */
        public void measure(int widthMeasureSpec, int heightMeasureSpec)
        {
            measure(widthMeasureSpec, heightMeasureSpec, this.aspectRatio);
        }

        /**
         * Measure with a specific aspect ratio<br />
         * <br />
         * After measuring, get the width and height with the {@link #getMeasuredWidth()}
         * and {@link #getMeasuredHeight()} methods, respectively.
         *
         * @param widthMeasureSpec  The width <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
         * @param heightMeasureSpec The height <tt>MeasureSpec</tt> passed in your <tt>View.onMeasure()</tt> method
         * @param aspectRatio       The aspect ratio to calculate measurements in respect to
         */
        public void measure(int widthMeasureSpec, int heightMeasureSpec, double aspectRatio)
        {
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSize = widthMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize(widthMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSize = heightMode == MeasureSpec.UNSPECIFIED ? Integer.MAX_VALUE : MeasureSpec.getSize(heightMeasureSpec);

            if (heightMode == MeasureSpec.EXACTLY && widthMode == MeasureSpec.EXACTLY)
            {
                /*
                 * Possibility 1: Both width and height fixed
                 */
                measuredWidth = widthSize;
                measuredHeight = heightSize;

            }
            else
                if (heightMode == MeasureSpec.EXACTLY)
                {
                    /*
                     * Possibility 2: Width dynamic, height fixed
                     */
                    measuredWidth = (int) Math.min(widthSize, heightSize * aspectRatio);
                    measuredHeight = (int) (measuredWidth / aspectRatio);

                }
                else
                    if (widthMode == MeasureSpec.EXACTLY)
                    {
                        /*
                         * Possibility 3: Width fixed, height dynamic
                         */
                        measuredHeight = (int) Math.min(heightSize, widthSize / aspectRatio);
                        measuredWidth = (int) (measuredHeight * aspectRatio);

                    }
                    else
                    {
                        /*
                         * Possibility 4: Both width and height dynamic
                         */
                        if (widthSize > heightSize * aspectRatio)
                        {
                            measuredHeight = heightSize;
                            measuredWidth = (int) (measuredHeight * aspectRatio);
                        }
                        else
                        {
                            measuredWidth = widthSize;
                            measuredHeight = (int) (measuredWidth / aspectRatio);
                        }

                    }
        }

        private Integer measuredWidth = null;

        /**
         * Get the width measured in the latest call to <tt>measure()</tt>.
         */
        public int getMeasuredWidth()
        {
            if (measuredWidth == null)
            {
                throw new IllegalStateException("You need to run measure() before trying to get measured dimensions");
            }
            return measuredWidth;
        }

        private Integer measuredHeight = null;

        /**
         * Get the height measured in the latest call to <tt>measure()</tt>.
         */
        public int getMeasuredHeight()
        {
            if (measuredHeight == null)
            {
                throw new IllegalStateException("You need to run measure() before trying to get measured dimensions");
            }
            return measuredHeight;
        }
    }
}