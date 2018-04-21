package de.xavaro.android.cam.streams;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Parameters;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;

import de.xavaro.android.cam.egl.EGLSurfaceView;
import de.xavaro.android.cam.packets.MediaCodecInputStream;
import de.xavaro.android.cam.util.NV21Converter;

/**
 * Don't use this class directly.
 */
public abstract class VideoStream extends MediaStream
{
    private static final String LOGTAG = VideoStream.class.getSimpleName();

    protected VideoQuality mRequestedQuality = VideoQuality.DEFAULT_VIDEO_QUALITY.clone();
    protected VideoQuality mQuality = mRequestedQuality.clone();
    protected SurfaceHolder.Callback mSurfaceHolderCallback = null;
    protected EGLSurfaceView mEGLSurfaceView = null;
    protected SharedPreferences mSettings = null;
    protected int mVideoEncoder, mCameraId = 0;
    protected int mRequestedOrientation = 0, mOrientation = 0;
    protected Camera mCamera;
    protected Thread mCameraThread;
    protected Looper mCameraLooper;

    protected boolean mCameraOpenedManually = true;
    protected boolean mFlashEnabled = false;
    protected boolean mSurfaceReady = false;
    protected boolean mUnlocked = false;
    protected boolean mPreviewStarted = false;
    protected boolean mUpdated = false;

    protected String mMimeType;
    protected String mEncoderName;
    protected int mEncoderColorFormat;
    protected int mCameraImageFormat;
    protected int mMaxFps = 0;

    /**
     * Don't use this class directly.
     * Uses CAMERA_FACING_BACK by default.
     */
    public VideoStream()
    {
        this(CameraInfo.CAMERA_FACING_BACK);
    }

    /**
     * Don't use this class directly
     *
     * @param camera Can be either CameraInfo.CAMERA_FACING_BACK or CameraInfo.CAMERA_FACING_FRONT
     */
    @SuppressLint("InlinedApi")
    public VideoStream(int camera)
    {
        super();
        setCamera(camera);
    }

    /**
     * Sets the camera that will be used to capture video.
     * You can call this method at any time and changes will take effect next time you start the stream.
     *
     * @param camera Can be either CameraInfo.CAMERA_FACING_BACK or CameraInfo.CAMERA_FACING_FRONT
     */
    public void setCamera(int camera)
    {
        CameraInfo cameraInfo = new CameraInfo();

        int numberOfCameras = Camera.getNumberOfCameras();

        for (int inx = 0; inx < numberOfCameras; inx++)
        {
            Camera.getCameraInfo(inx, cameraInfo);

            if (cameraInfo.facing == camera)
            {
                mCameraId = inx;
                break;
            }
        }
    }

    /**
     * Switch between the front facing and the back facing camera of the phone.
     * If {@link #startPreview()} has been called, the preview will be  briefly interrupted.
     * If {@link #start()} has been called, the stream will be  briefly interrupted.
     * You should not call this method from the main thread if you are already streaming.
     *
     * @throws IOException
     * @throws RuntimeException
     **/
    public void switchCamera() throws RuntimeException, IOException
    {
        if (Camera.getNumberOfCameras() == 1)
            throw new IllegalStateException("Phone only has one camera !");
        boolean streaming = mStreaming;
        boolean previewing = mCamera != null && mCameraOpenedManually;
        mCameraId = (mCameraId == CameraInfo.CAMERA_FACING_BACK) ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK;
        setCamera(mCameraId);
        stopPreview();
        mFlashEnabled = false;
        if (previewing) startPreview();
        if (streaming) start();
    }

    /**
     * Returns the id of the camera currently selected.
     * Can be either {@link CameraInfo#CAMERA_FACING_BACK} or
     * {@link CameraInfo#CAMERA_FACING_FRONT}.
     */
    public int getCamera()
    {
        return mCameraId;
    }

    /**
     * Sets a Surface to show a preview of recorded media (video).
     * You can call this method at any time and changes will take effect next time you call {@link #start()}.
     */
    public synchronized void setSurfaceView(EGLSurfaceView view)
    {
        mEGLSurfaceView = view;

        if (mSurfaceHolderCallback != null && mEGLSurfaceView != null && mEGLSurfaceView.getHolder() != null)
        {
            mEGLSurfaceView.getHolder().removeCallback(mSurfaceHolderCallback);
        }

        if (mEGLSurfaceView != null && mEGLSurfaceView.getHolder() != null)
        {
            mSurfaceHolderCallback = new Callback()
            {
                @Override
                public void surfaceDestroyed(SurfaceHolder holder)
                {
                    mSurfaceReady = false;
                    stopPreview();
                    Log.d(LOGTAG, "Surface destroyed !");
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder)
                {
                    mSurfaceReady = true;
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
                {
                    Log.d(LOGTAG, "Surface Changed !");
                }
            };
            mEGLSurfaceView.getHolder().addCallback(mSurfaceHolderCallback);
            mSurfaceReady = true;
        }
    }

    /**
     * Turns the LED on or off if phone has one.
     */
    public synchronized void setFlashState(boolean state)
    {
        // If the camera has already been opened, we apply the change immediately
        if (mCamera != null)
        {

            if (mStreaming && mMode == MODE_MEDIARECORDER_API)
            {
                lockCamera();
            }

            Parameters parameters = mCamera.getParameters();

            // We test if the phone has a flash
            if (parameters.getFlashMode() == null)
            {
                // The phone has no flash or the choosen camera can not toggle the flash
                throw new RuntimeException("Can't turn the flash on !");
            }
            else
            {
                parameters.setFlashMode(state ? Parameters.FLASH_MODE_TORCH : Parameters.FLASH_MODE_OFF);
                try
                {
                    mCamera.setParameters(parameters);
                    mFlashEnabled = state;
                }
                catch (RuntimeException e)
                {
                    mFlashEnabled = false;
                    throw new RuntimeException("Can't turn the flash on !");
                }
                finally
                {
                    if (mStreaming && mMode == MODE_MEDIARECORDER_API)
                    {
                        unlockCamera();
                    }
                }
            }
        }
        else
        {
            mFlashEnabled = state;
        }
    }

    /**
     * Toggles the LED of the phone if it has one.
     * You can get the current state of the flash with {@link VideoStream#getFlashState()}.
     */
    public synchronized void toggleFlash()
    {
        setFlashState(!mFlashEnabled);
    }

    /**
     * Indicates whether or not the flash of the phone is on.
     */
    public boolean getFlashState()
    {
        return mFlashEnabled;
    }

    /**
     * Sets the orientation of the preview.
     *
     * @param orientation The orientation of the preview
     */
    public void setPreviewOrientation(int orientation)
    {
        mRequestedOrientation = orientation;
        mUpdated = false;
    }

    /**
     * Sets the configuration of the stream. You can call this method at any time
     * and changes will take effect next time you call {@link #configure()}.
     *
     * @param videoQuality Quality of the stream
     */
    public void setVideoQuality(VideoQuality videoQuality)
    {
        if (!mRequestedQuality.equals(videoQuality))
        {
            mRequestedQuality = videoQuality.clone();
            mUpdated = false;
        }
    }

    /**
     * Returns the quality of the stream.
     */
    public VideoQuality getVideoQuality()
    {
        return mRequestedQuality;
    }

    /**
     * Some data (SPS and PPS params) needs to be stored when {@link #getSessionDescription()} is called
     *
     * @param prefs The SharedPreferences that will be used to save SPS and PPS parameters
     */
    public void setPreferences(SharedPreferences prefs)
    {
        mSettings = prefs;
    }

    /**
     * Configures the stream. You need to call this before calling {@link #getSessionDescription()}
     * to apply your configuration of the stream.
     */
    public synchronized void configure() throws IllegalStateException, IOException
    {
        super.configure();
        mOrientation = mRequestedOrientation;
    }

    /**
     * Starts the stream.
     * This will also open the camera and display the preview
     * if {@link #startPreview()} has not already been called.
     */
    public synchronized void start() throws IllegalStateException, IOException
    {
        if (!mPreviewStarted) mCameraOpenedManually = false;
        super.start();
        Log.d(LOGTAG, "Stream configuration: FPS: " + mQuality.framerate + " Width: " + mQuality.resX + " Height: " + mQuality.resY);
    }

    /**
     * Stops the stream.
     */
    public synchronized void stop()
    {
        if (mCamera != null)
        {
            if (mMode == MODE_MEDIACODEC_API)
            {
                mCamera.setPreviewCallbackWithBuffer(null);
            }
            if (mMode == MODE_MEDIACODEC_API_2)
            {
                ((EGLSurfaceView) mEGLSurfaceView).removeMediaCodecSurface();
            }
            super.stop();
            // We need to restart the preview
            if (!mCameraOpenedManually)
            {
                destroyCamera();
            }
            else
            {
                try
                {
                    startPreview();
                }
                catch (RuntimeException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void startPreview()
            throws RuntimeException
    {

        mCameraOpenedManually = true;
        if (!mPreviewStarted)
        {
            createCamera();
            updateCamera();
        }
    }

    /**
     * Stops the preview.
     */
    public synchronized void stopPreview()
    {
        mCameraOpenedManually = false;
        stop();
    }

    /**
     * Video encoding is done by a MediaCodec.
     */
    protected void encodeWithMediaCodec() throws RuntimeException, IOException
    {
        if (mMode == MODE_MEDIACODEC_API_2)
        {
            // Uses the method MediaCodec.createInputSurface to feed the encoder
            encodeWithMediaCodecMethod2();
        }
        else
        {
            // Uses dequeueInputBuffer to feed the encoder
            encodeWithMediaCodecMethod1();
        }
    }

    /**
     * Video encoding is done by a MediaCodec.
     */
    @SuppressLint("NewApi")
    protected void encodeWithMediaCodecMethod1() throws RuntimeException, IOException
    {

        Log.d(LOGTAG, "Video encoded using the MediaCodec API with a buffer");

        // Updates the parameters of the camera if needed
        createCamera();
        updateCamera();

        // Estimates the frame rate of the camera
        measureFramerate();

        // Starts the preview if needed
        if (!mPreviewStarted)
        {
            try
            {
                mCamera.startPreview();
                mPreviewStarted = true;
            }
            catch (RuntimeException e)
            {
                destroyCamera();
                throw e;
            }
        }

        final NV21Converter convertor = new NV21Converter(mQuality.resX, mQuality.resY, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);

        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", mQuality.resX, mQuality.resY);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, mQuality.bitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mQuality.framerate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();

        Camera.PreviewCallback callback = new Camera.PreviewCallback()
        {
            long now = System.nanoTime() / 1000, oldnow = now, i = 0;
            ByteBuffer[] inputBuffers = mMediaCodec.getInputBuffers();

            @Override
            public void onPreviewFrame(byte[] data, Camera camera)
            {
                try
                {
                    int bufferIndex = mMediaCodec.dequeueInputBuffer(500000);

                    if (bufferIndex >= 0)
                    {
                        inputBuffers[bufferIndex].clear();

                        if (data == null)
                        {
                            Log.e(LOGTAG, "Symptom of the \"Callback buffer was to small\" problem...");
                        }
                        else
                        {
                            convertor.convert(data, inputBuffers[bufferIndex]);
                        }
                        
                        mMediaCodec.queueInputBuffer(bufferIndex,
                                0, inputBuffers[bufferIndex].position(),
                                System.nanoTime() / 1000,
                                0);
                    }
                    else
                    {
                        Log.e(LOGTAG, "No buffer available !");
                    }
                }
                finally
                {
                    mCamera.addCallbackBuffer(data);
                }
            }
        };

        for (int inx = 0; inx < 10; inx++) mCamera.addCallbackBuffer(new byte[convertor.getBufferSize()]);

        mCamera.setPreviewCallbackWithBuffer(callback);

        mPacketizer.setInputStream(new MediaCodecInputStream(mMediaCodec));
        mPacketizer.start();

        mStreaming = true;
    }

    /**
     * Video encoding is done by a MediaCodec.
     * But here we will use the buffer-to-surface method
     */
    @SuppressLint({"InlinedApi", "NewApi"})
    protected void encodeWithMediaCodecMethod2() throws RuntimeException, IOException
    {
        Log.d(LOGTAG, "Video encoded using the MediaCodec API with a surface");

        createCamera();
        updateCamera();

        measureFramerate();

        Surface surface = mMediaCodec.createInputSurface();
        mEGLSurfaceView.addMediaCodecSurface(surface);

        MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", mQuality.resX, mQuality.resY);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, mQuality.bitrate);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mQuality.framerate);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

        mMediaCodec = MediaCodec.createEncoderByType("video/avc");
        mMediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        mMediaCodec.start();

        mPacketizer.setInputStream(new MediaCodecInputStream(mMediaCodec));
        mPacketizer.start();

        mStreaming = true;
    }

    /**
     * Returns a description of the stream using SDP.
     * This method can only be called after {@link Stream#configure()}.
     *
     * @throws IllegalStateException Thrown when {@link Stream#configure()} wa not called.
     */
    public abstract String getSessionDescription() throws IllegalStateException;

    /**
     * Opens the camera in a new Looper thread so that the preview callback is not called from the main thread
     * If an exception is thrown in this Looper thread, we bring it back into the main thread.
     *
     * @throws RuntimeException Might happen if another app is already using the camera.
     */
    private void openCamera() throws RuntimeException
    {
        final Semaphore lock = new Semaphore(0);
        final RuntimeException[] exception = new RuntimeException[1];
        mCameraThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Looper.prepare();
                mCameraLooper = Looper.myLooper();
                try
                {
                    mCamera = Camera.open(mCameraId);
                }
                catch (RuntimeException e)
                {
                    exception[0] = e;
                }
                finally
                {
                    lock.release();
                    Looper.loop();
                }
            }
        });
        mCameraThread.start();
        lock.acquireUninterruptibly();

        if (exception[0] != null)
        {
            Log.e(LOGTAG, "Camera in use !");
        }
    }

    protected synchronized void createCamera() throws RuntimeException
    {
        if (mEGLSurfaceView == null)
        {
            Log.e(LOGTAG, "Invalid surface !");
            return;
        }

        if (mEGLSurfaceView.getHolder() == null || !mSurfaceReady)
        {
            Log.e(LOGTAG, "Invalid surface !");
            return;
        }

        if (mCamera == null)
        {
            openCamera();
            mUpdated = false;
            mUnlocked = false;
            mCamera.setErrorCallback(new Camera.ErrorCallback()
            {
                @Override
                public void onError(int error, Camera camera)
                {
                    // On some phones when trying to use the camera facing front the media server will die
                    // Whether or not this callback may be called really depends on the phone
                    if (error == Camera.CAMERA_ERROR_SERVER_DIED)
                    {
                        // In this case the application must release the camera and instantiate a new one
                        Log.e(LOGTAG, "Media server died !");
                        // We don't know in what thread we are so stop needs to be synchronized
                        mCameraOpenedManually = false;
                        stop();
                    }
                    else
                    {
                        Log.e(LOGTAG, "Error unknown with the camera: " + error);
                    }
                }
            });

            try
            {

                // If the phone has a flash, we turn it on/off according to mFlashEnabled
                // setRecordingHint(true) is a very nice optimization if you plane to only use the Camera for recording
                Parameters parameters = mCamera.getParameters();
                if (parameters.getFlashMode() != null)
                {
                    parameters.setFlashMode(mFlashEnabled ? Parameters.FLASH_MODE_TORCH : Parameters.FLASH_MODE_OFF);
                }
                parameters.setRecordingHint(true);
                mCamera.setParameters(parameters);
                mCamera.setDisplayOrientation(mOrientation);

                try
                {
                    if (mMode == MODE_MEDIACODEC_API_2)
                    {
                        mEGLSurfaceView.startGLThread();
                        mCamera.setPreviewTexture(mEGLSurfaceView.getSurfaceTexture());
                    }
                    else
                    {
                        mCamera.setPreviewDisplay(mEGLSurfaceView.getHolder());
                    }
                }
                catch (IOException ex)
                {
                    Log.e(LOGTAG, "Invalid surface !");
                    return;
                }

            }
            catch (RuntimeException e)
            {
                destroyCamera();
                throw e;
            }

        }
    }

    protected synchronized void destroyCamera()
    {
        if (mCamera != null)
        {
            if (mStreaming) super.stop();
            lockCamera();
            mCamera.stopPreview();
            try
            {
                mCamera.release();
            }
            catch (Exception e)
            {
                Log.e(LOGTAG, e.getMessage() != null ? e.getMessage() : "unknown error");
            }
            mCamera = null;
            mCameraLooper.quit();
            mUnlocked = false;
            mPreviewStarted = false;
        }
    }

    protected synchronized void updateCamera() throws RuntimeException
    {

        // The camera is already correctly configured
        if (mUpdated) return;

        if (mPreviewStarted)
        {
            mPreviewStarted = false;
            mCamera.stopPreview();
        }

        Parameters parameters = mCamera.getParameters();
        mQuality = VideoQuality.determineClosestSupportedResolution(parameters, mQuality);
        int[] max = VideoQuality.determineMaximumSupportedFramerate(parameters);

        double ratio = (double) mQuality.resX / (double) mQuality.resY;
        mEGLSurfaceView.requestAspectRatio(ratio);

        parameters.setPreviewFormat(mCameraImageFormat);
        parameters.setPreviewSize(mQuality.resX, mQuality.resY);
        parameters.setPreviewFpsRange(max[0], max[1]);

        try
        {
            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(mOrientation);
            mCamera.startPreview();
            mPreviewStarted = true;
            mUpdated = true;
        }
        catch (RuntimeException e)
        {
            destroyCamera();
            throw e;
        }
    }

    protected void lockCamera()
    {
        if (mUnlocked)
        {
            Log.d(LOGTAG, "Locking camera");
            try
            {
                mCamera.reconnect();
            }
            catch (Exception e)
            {
                Log.e(LOGTAG, e.getMessage());
            }
            mUnlocked = false;
        }
    }

    protected void unlockCamera()
    {
        if (!mUnlocked)
        {
            Log.d(LOGTAG, "Unlocking camera");
            try
            {
                mCamera.unlock();
            }
            catch (Exception e)
            {
                Log.e(LOGTAG, e.getMessage());
            }
            mUnlocked = true;
        }
    }


    /**
     * Computes the average frame rate at which the preview callback is called.
     * We will then use this average frame rate with the MediaCodec.
     * Blocks the thread in which this function is called.
     */
    private void measureFramerate()
    {
        final Semaphore lock = new Semaphore(0);

        final Camera.PreviewCallback callback = new Camera.PreviewCallback()
        {
            int i = 0, t = 0;
            long now, oldnow, count = 0;

            @Override
            public void onPreviewFrame(byte[] data, Camera camera)
            {
                i++;
                now = System.nanoTime() / 1000;
                if (i > 3)
                {
                    t += now - oldnow;
                    count++;
                }
                if (i > 20)
                {
                    mQuality.framerate = (int) (1000000 / (t / count) + 1);
                    lock.release();
                }
                oldnow = now;
            }
        };

        mCamera.setPreviewCallback(callback);

        try
        {
            lock.tryAcquire(2, TimeUnit.SECONDS);
            Log.d(LOGTAG, "Actual framerate: " + mQuality.framerate);
            if (mSettings != null)
            {
                Editor editor = mSettings.edit();
                editor.putInt(PREF_PREFIX + "fps" + mRequestedQuality.framerate + "," + mCameraImageFormat + "," + mRequestedQuality.resX + mRequestedQuality.resY, mQuality.framerate);
                editor.commit();
            }
        }
        catch (InterruptedException e)
        {
        }

        mCamera.setPreviewCallback(null);

    }

}