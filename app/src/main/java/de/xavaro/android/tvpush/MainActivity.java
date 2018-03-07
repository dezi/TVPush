package de.xavaro.android.tvpush;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.view.WindowManager;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.os.Bundle;
import android.util.Log;

import zz.top.cam.Camera;
import zz.top.cam.Cameras;

import java.nio.ByteBuffer;

import de.xavaro.android.common.Simple;

public class MainActivity extends AppCompatActivity implements SurfaceHolder.Callback
{
    private final static String LOGTAG = MainActivity.class.getSimpleName();

    private TextView voiceButton;

    private Camera camera;
    private FrameLayout topframe;
    private FrameLayout videoView;

    private SpeechRecognition recognition;

    private static final String SAMPLE = Environment.getExternalStorageDirectory() + "/video.mp4";
    private PlayerThread mPlayer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setFinishOnTouchOutside(false);

        WindowManager.LayoutParams params = getWindow().getAttributes();

        params.alpha = 1.0f;
        params.dimAmount = 0.0f;
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.format = PixelFormat.TRANSLUCENT;

        getWindow().setAttributes(params);

        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if (height > width)
        {
            //getWindow().setLayout((int) (width * 0.4), (int) (height * 0.4));
            getWindow().setLayout(700, 500);
        }
        else
        {
            //getWindow().setLayout((int) (width * 0.4), (int) (height * 0.4));
            getWindow().setLayout(700, 500);
        }

        topframe = new FrameLayout(this);
        topframe.setBackgroundColor(0x88880000);
        setContentView(topframe);

        TextView heloButton = new TextView(this);
        heloButton.setText("HELO");
        heloButton.setTextSize(36);
        heloButton.setTextColor(Color.WHITE);
        heloButton.setPadding(50, 50, 50, 50);

        heloButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                RegistrationService.requestHello(view.getContext());
            }
        });

        topframe.addView(heloButton, new FrameLayout.LayoutParams(Simple.WC, Simple.WC, Gravity.BOTTOM + Gravity.START));

        voiceButton = new TextView(this);
        voiceButton.setText("VOICE ON");
        voiceButton.setTextSize(36);
        voiceButton.setTextColor(Color.WHITE);
        voiceButton.setPadding(50, 50, 50, 50);

        voiceButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                speechClick();
            }
        });

        topframe.addView(voiceButton, new FrameLayout.LayoutParams(Simple.WC, Simple.WC, Gravity.BOTTOM + Gravity.END));

        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                String name = "Dezi's Domcam #1";

                camera = Cameras.createCameraByName(name);

                if (camera == null)
                {
                    Log.d(LOGTAG, "createCameraByName not fund name=" + name);
                }
                else
                {
                    videoView = camera.createSurface(MainActivity.this);

                    topframe.addView(videoView, new FrameLayout.LayoutParams(640, 360, Gravity.TOP + Gravity.START));

                    camera.connectCamera();

                    camera.setResolution(Camera.RESOLUTION_720P);

                    //camera.startRealtimeVideo();
                    //camera.startRealtimeAudio();
                    //camera.startFaceDetection(true);
                }
            }

        }, 2000);
    }

    private void speechClick()
    {
        if (recognition == null)
        {
            recognition = new SpeechRecognition(this);
        }

        String text = voiceButton.getText().toString();

        if (text.equals("VOICE ON"))
        {
            voiceButton.setText("VOICE OFF");
            recognition.startListening();
        }
        else
        {
            voiceButton.setText("VOICE ON");
            recognition.stopListening();
        }
    }

    @Override
    public void onStart()
    {
        super.onStart();

        Log.d(LOGTAG, "onStart:");
    }

    @Override
    public void onResume()
    {
        super.onResume();

        Log.d(LOGTAG, "onResume:");
    }

    @Override
    public void onPause()
    {
        super.onPause();

        Log.d(LOGTAG, "onPause:");
    }

    @Override
    public void onStop()
    {
        super.onStop();

        Log.d(LOGTAG, "onStop:");
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
    }

    @Override
    public void surfaceChanged(final SurfaceHolder holder, int format, int width, int height)
    {
        Log.d(LOGTAG, "surfaceChanged: width=" + width + " height=" + height);

        /*
        if (mPlayer == null)
        {
            mPlayer = new PlayerThread(holder.getSurface());
            mPlayer.start();
        }
        */
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
    }

    private class PlayerThread extends Thread
    {
        private MediaExtractor extractor;
        private MediaCodec decoder;
        private Surface surface;

        public PlayerThread(Surface surface)
        {
            this.surface = surface;
        }

        @Override
        public void run()
        {
            try
            {
                extractor = new MediaExtractor();
                extractor.setDataSource(SAMPLE);

                for (int i = 0; i < extractor.getTrackCount(); i++)
                {
                    MediaFormat format = extractor.getTrackFormat(i);

                    Log.d(LOGTAG, "PlayerThread format=" + format);

                    String mime = format.getString(MediaFormat.KEY_MIME);
                    if (mime.startsWith("video/"))
                    {
                        extractor.selectTrack(i);
                        decoder = MediaCodec.createDecoderByType(mime);
                        decoder.configure(format, surface, null, 0);
                        break;
                    }
                }

                if (decoder == null)
                {
                    Log.e("DecodeActivity", "Can't find video info!");
                    return;
                }

                decoder.start();

                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                boolean isEOS = false;
                long startMs = System.currentTimeMillis();

                while (!Thread.interrupted())
                {
                    if (!isEOS)
                    {
                        int inIndex = decoder.dequeueInputBuffer(10000);
                        if (inIndex >= 0)
                        {
                            ByteBuffer buffer = decoder.getInputBuffer(inIndex);
                            int sampleSize = extractor.readSampleData(buffer, 0);
                            if (sampleSize < 0)
                            {
                                // We shouldn't stop the playback at this point, just pass the EOS
                                // flag to decoder, we will get it again from the
                                // dequeueOutputBuffer
                                Log.d("DecodeActivity", "InputBuffer BUFFER_FLAG_END_OF_STREAM");
                                decoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                isEOS = true;
                            }
                            else
                            {
                                //decoder.queueInputBuffer(inIndex, 0, sampleSize, extractor.getSampleTime(), 0);
                                decoder.queueInputBuffer(inIndex, 0, sampleSize, 0, 0);
                                extractor.advance();
                            }
                        }
                    }

                    int outIndex = decoder.dequeueOutputBuffer(info, 10000);

                    switch (outIndex)
                    {
                        case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                            Log.d("DecodeActivity", "New format " + decoder.getOutputFormat());
                            break;
                        case MediaCodec.INFO_TRY_AGAIN_LATER:
                            Log.d("DecodeActivity", "dequeueOutputBuffer timed out!");
                            break;
                        default:
                            if (outIndex < 0) break;
                            ByteBuffer buffer = decoder.getOutputBuffer(outIndex);
                            Log.v("DecodeActivity", "We can't use this buffer but render it due to the API limit, " + buffer);

                            MediaFormat format = decoder.getOutputFormat();
                            Log.d(LOGTAG, "PlayerThread:" + format);
                            //colorformat = 2141391876;

                            // We use a very simple clock to keep the video FPS, or the video
                            // playback will be too fast
                            while (info.presentationTimeUs / 1000 > System.currentTimeMillis() - startMs)
                            {
                                try
                                {
                                    sleep(10);
                                }
                                catch (InterruptedException e)
                                {
                                    e.printStackTrace();
                                    break;
                                }
                            }
                            decoder.releaseOutputBuffer(outIndex, true);
                            break;
                    }

                    // All decoded frames have been rendered, we can stop playing now
                    if ((info.flags & MediaCodec.BUFFER_FLAG_END_OF_STREAM) != 0)
                    {
                        Log.d("DecodeActivity", "OutputBuffer BUFFER_FLAG_END_OF_STREAM");
                        break;
                    }
                }

                decoder.stop();
                decoder.release();
                extractor.release();
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }

}
