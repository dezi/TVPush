package de.xavaro.android.cam.util;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.hardware.Camera;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.List;

import de.xavaro.android.cam.simple.Json;

public class CAMGetVideoModes
{
    private static final String LOGTAG = CAMGetVideoModes.class.getSimpleName();

    private static MediaCodec mEncoder;
    private static JSONArray configs;
    private static byte[] data;

    public static void getVideoModes()
    {
        configs = MP4Config.getConfigs();

        if (configs != null)
        {
            Log.d(LOGTAG, "getVideoModes: configs already setup.");
            return;
        }

        Log.d(LOGTAG, "getVideoModes: start.");

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                configs = new JSONArray();

                try
                {
                    Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    Camera.Parameters params = camera.getParameters();

                    List<Camera.Size> sizes = params.getSupportedPreviewSizes();

                    for (Camera.Size size : sizes)
                    {
                        for (int inx = 0; inx < NV21Converter.formatsWeLike.length; inx++)
                        {
                            checkMedia(size.width, size.height, 10, NV21Converter.formatsWeLike[ inx ]);
                            checkMedia(size.width, size.height, 20, NV21Converter.formatsWeLike[ inx ]);
                            checkMedia(size.width, size.height, 24, NV21Converter.formatsWeLike[ inx ]);
                            checkMedia(size.width, size.height, 25, NV21Converter.formatsWeLike[ inx ]);
                            checkMedia(size.width, size.height, 30, NV21Converter.formatsWeLike[ inx ]);
                        }
                    }
                }
                catch (Exception ignore)
                {
                    Log.e(LOGTAG, "getVideoModes: camera problem!");
                }

                Log.d(LOGTAG, "getVideoModes: modes=" + Json.toPretty(configs));

                MP4Config.setConfigs(configs);

                Log.d(LOGTAG, "getVideoModes: configs saved.");
            }
        });

        thread.start();
    }

    private static void checkMedia(int width, int height, int fps, int cformat)
    {
        SPS = null;
        PPS = null;

        try
        {
            MediaFormat mediaFormat = MediaFormat.createVideoFormat("video/avc", width, height);
            mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, 500 * 1000);
            mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, fps);
            mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, cformat);
            mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);

            mEncoder = MediaCodec.createEncoderByType("video/avc");
            mEncoder.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncoder.start();

            NV21Converter nv21 = new NV21Converter(width, height, cformat);
            data = nv21.convert(nv21.createTestImage());

            searchSPSandPPS(fps);

            Log.d(LOGTAG, "checkMedia:"
                    + " width=" + width
                    + " height=" + height
                    + " fps=" + fps
                    + " cformat=" + cformat
                    + " pps=" + PPS
                    + " sps=" + SPS
            );

            if ((PPS != null) && (SPS != null))
            {
                JSONObject config = new JSONObject();

                Json.put(config, "width", width);
                Json.put(config, "height", height);
                Json.put(config, "fps", fps);
                Json.put(config, "cformat", cformat);
                Json.put(config, "PPS", PPS);
                Json.put(config, "SPS", SPS);

                Json.put(configs, config);
            }
        }
        catch (Exception ex)
        {
            //Log.d(LOGTAG, "checkMedia: encoder knallt.");
        }
        finally
        {
            if (mEncoder != null)
            {
                try
                {
                    mEncoder.stop();
                }
                catch (Exception ignore)
                {
                }
                try
                {
                    mEncoder.release();
                }
                catch (Exception ignore)
                {
                }
            }
        }
    }

    private static long timestamp()
    {
        return System.nanoTime() / 1000;
    }

    private static String SPS;
    private static String PPS;

    private static void searchSPSandPPS(int fps)
    {
        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
        ByteBuffer[] outputBuffers = mEncoder.getOutputBuffers();

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        byte[] csd = new byte[128];
        int p = 4;
        int q = 4;
        int len;

        long now = timestamp();
        long elapsed = 0;

        while (elapsed < 3000000 && (SPS == null || PPS == null))
        {
            int bufferIndex = mEncoder.dequeueInputBuffer(1000000 / fps);

            if (bufferIndex >= 0)
            {
                inputBuffers[bufferIndex].clear();
                inputBuffers[bufferIndex].put(data, 0, data.length);
                mEncoder.queueInputBuffer(bufferIndex, 0, data.length, timestamp(), 0);
            }
            else
            {
                Log.e(LOGTAG, "No buffer available !");
            }

            // We are looking for the SPS and the PPS here. As always, Android is very inconsistent, I have observed that some
            // encoders will give those parameters through the MediaFormat object (that is the normal behaviour).
            // But some other will not, in that case we try to find a NAL unit of type 7 or 8 in the byte stream outputed by the encoder...

            int index = mEncoder.dequeueOutputBuffer(info, 1000000 / fps);

            byte[] mSPS;
            byte[] mPPS;

            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
            {
                MediaFormat format = mEncoder.getOutputFormat();

                ByteBuffer spsb = format.getByteBuffer("csd-0");
                ByteBuffer ppsb = format.getByteBuffer("csd-1");

                mSPS = new byte[spsb.capacity() - 4];
                spsb.position(4);
                spsb.get(mSPS, 0, mSPS.length);

                mPPS = new byte[ppsb.capacity() - 4];
                ppsb.position(4);
                ppsb.get(mPPS, 0, mPPS.length);

                SPS = toHexString(mSPS);
                PPS = toHexString(mPPS);

                break;
            }
            else
                if (index == MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED)
                {
                    outputBuffers = mEncoder.getOutputBuffers();
                }
                else
                    if (index >= 0)
                    {

                        len = info.size;
                        if (len < 128)
                        {
                            outputBuffers[index].get(csd, 0, len);
                            if (len > 0 && csd[0] == 0 && csd[1] == 0 && csd[2] == 0 && csd[3] == 1)
                            {
                                while (p < len)
                                {
                                    //noinspection PointlessArithmeticExpression
                                    while (!(csd[p + 0] == 0 && csd[p + 1] == 0 && csd[p + 2] == 0 && csd[p + 3] == 1) && p + 3 < len)
                                        p++;
                                    if (p + 3 >= len) p = len;
                                    if ((csd[q] & 0x1F) == 7)
                                    {
                                        mSPS = new byte[p - q];
                                        System.arraycopy(csd, q, mSPS, 0, p - q);

                                        SPS = toHexString(mSPS);
                                    }
                                    else
                                    {
                                        mPPS = new byte[p - q];
                                        System.arraycopy(csd, q, mPPS, 0, p - q);

                                        PPS = toHexString(mPPS);
                                    }
                                    p += 4;
                                    q = p;
                                }
                            }
                        }

                        mEncoder.releaseOutputBuffer(index, false);
                    }

            elapsed = timestamp() - now;
        }
    }

    private static String toHexString(byte[] buffer)
    {
        String c;
        StringBuilder s = new StringBuilder();

        for (byte aBuffer : buffer)
        {
            c = Integer.toHexString(aBuffer & 0xFF);
            s.append(c.length() < 2 ? "0" + c : c);
        }

        return s.toString();
    }

}
