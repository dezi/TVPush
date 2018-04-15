package de.xavaro.android.cam.util;

import android.content.Context;
import android.hardware.Camera;
import android.icu.text.RelativeDateTimeFormatter;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceView;

import java.io.File;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CAMGetVideoModes
{
    private static final String LOGTAG = CAMGetVideoModes.class.getSimpleName();

    private static Semaphore mLock = new Semaphore(0);

    public static void getVideoModes(final Context context, final SurfaceView surfaceView)
    {
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                checkMedia(context, surfaceView);
            }
        });

        thread.start();
    }

    private static void checkMedia(Context context, SurfaceView surfaceView)
    {
        File testFile = new File(context.getCacheDir(), "CAMGetVideoModes.mp4");
        MediaRecorder mMediaRecorder = new MediaRecorder();
        ;

        try
        {
            Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            camera.setPreviewDisplay(surfaceView.getHolder());
            camera.startPreview();
            camera.unlock();

            mMediaRecorder.setCamera(camera);
            mMediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setVideoSize(640, 480);
            mMediaRecorder.setVideoFrameRate(24);
            mMediaRecorder.setVideoEncodingBitRate(50 * 1000);
            mMediaRecorder.setOutputFile(testFile.toString());
            mMediaRecorder.setMaxDuration(3000);

            mMediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener()
            {
                public void onInfo(MediaRecorder mr, int what, int extra)
                {
                    Log.d(LOGTAG, "MediaRecorder callback called !");

                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED)
                    {
                        Log.d(LOGTAG, "MediaRecorder: MAX_DURATION_REACHED");
                    }
                    else
                        if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED)
                        {
                            Log.d(LOGTAG, "MediaRecorder: MAX_FILESIZE_REACHED");
                        }
                        else
                            if (what == MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN)
                            {
                                Log.d(LOGTAG, "MediaRecorder: INFO_UNKNOWN");
                            }
                            else
                            {
                                Log.d(LOGTAG, "WTF ?");
                            }
                    mLock.release();
                }
            });

            Log.d(LOGTAG, "MediaRecorder prepare");

            mMediaRecorder.prepare();
            Log.d(LOGTAG, "MediaRecorder start");
            mMediaRecorder.start();
            Log.d(LOGTAG, "MediaRecorder started.");

            if (mLock.tryAcquire(12, TimeUnit.SECONDS))
            {
                Log.d(LOGTAG, "MediaRecorder callback was called :)");
                Thread.sleep(400);
            }
            else
            {
                Log.d(LOGTAG, "MediaRecorder callback was not called after 6 seconds... :(");
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        finally
        {
            try
            {
                mMediaRecorder.stop();
            }
            catch (Exception ignore)
            {
            }

            mMediaRecorder.release();
        }

        try
        {
            CAMMP4Config config = new CAMMP4Config(testFile.toString());

            config.getHEXPPS();
            config.getHEXSPS();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        // Delete dummy video
        if (!testFile.delete()) Log.e(LOGTAG, "Temp file could not be erased");

        Log.i(LOGTAG, "H264 Test succeded...");

    }
}
