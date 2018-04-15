package de.xavaro.android.cam.util;

import android.media.MediaCodec;
import android.media.MediaFormat;
import android.hardware.Camera;
import android.util.Log;

import java.nio.ByteBuffer;
import java.util.List;

public class CAMGetVideoModes
{
    private static final String LOGTAG = CAMGetVideoModes.class.getSimpleName();

    public static void getVideoModes()
    {
        Log.d(LOGTAG, "getVideoModes: start.");

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    Camera camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
                    Camera.Parameters params = camera.getParameters();

                    List<Camera.Size> sizes = params.getSupportedPreviewSizes();

                    for (Camera.Size size : sizes)
                    {
                        for (int inx = 0; inx < NV21Converter.colorWeLike.length; inx++)
                        {
                            checkMedia(size.width, size.height, 10, NV21Converter.colorWeLike[ inx ]);
                            checkMedia(size.width, size.height, 20, NV21Converter.colorWeLike[ inx ]);
                            checkMedia(size.width, size.height, 24, NV21Converter.colorWeLike[ inx ]);
                            checkMedia(size.width, size.height, 25, NV21Converter.colorWeLike[ inx ]);
                            checkMedia(size.width, size.height, 30, NV21Converter.colorWeLike[ inx ]);
                        }
                    }
                }
                catch (Exception ignore)
                {
                    Log.d(LOGTAG, "getVideoModes: thread knall.");

                    ignore.printStackTrace();
                }
            }
        });

        thread.start();
    }

    private static MediaCodec mEncoder;
    private static byte[] data;

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

            NV21Converter nv21 = new NV21Converter(width, height);
            nv21.setYPadding(0);
            nv21.setEncoderColorFormat(cformat);

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

    private static byte[] mSPS;
    private static byte[] mPPS;

    private static String SPS;
    private static String PPS;

    private static long searchSPSandPPS(int fps)
    {
        ByteBuffer[] inputBuffers = mEncoder.getInputBuffers();
        ByteBuffer[] outputBuffers = mEncoder.getOutputBuffers();
        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
        byte[] csd = new byte[128];
        int len = 0, p = 4, q = 4;
        long elapsed = 0, now = timestamp();

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

            if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED)
            {

                // The PPS and PPS shoud be there
                MediaFormat format = mEncoder.getOutputFormat();
                ByteBuffer spsb = format.getByteBuffer("csd-0");
                ByteBuffer ppsb = format.getByteBuffer("csd-1");
                mSPS = new byte[spsb.capacity() - 4];
                spsb.position(4);
                spsb.get(mSPS, 0, mSPS.length);
                mPPS = new byte[ppsb.capacity() - 4];
                ppsb.position(4);
                ppsb.get(mPPS, 0, mPPS.length);

                SPS = toHexString(mSPS, 0, mSPS.length);
                PPS = toHexString(mPPS, 0, mPPS.length);
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
                                // Parses the SPS and PPS, they could be in two different packets and in a different order
                                //depending on the phone so we don't make any assumption about that
                                while (p < len)
                                {
                                    while (!(csd[p + 0] == 0 && csd[p + 1] == 0 && csd[p + 2] == 0 && csd[p + 3] == 1) && p + 3 < len)
                                        p++;
                                    if (p + 3 >= len) p = len;
                                    if ((csd[q] & 0x1F) == 7)
                                    {
                                        mSPS = new byte[p - q];
                                        System.arraycopy(csd, q, mSPS, 0, p - q);

                                        SPS = toHexString(mSPS, 0, mSPS.length);
                                    }
                                    else
                                    {
                                        mPPS = new byte[p - q];
                                        System.arraycopy(csd, q, mPPS, 0, p - q);

                                        PPS = toHexString(mPPS, 0, mPPS.length);
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

        return elapsed;
    }

    static String toHexString(byte[] buffer, int start, int len)
    {
        String c;
        StringBuilder s = new StringBuilder();
        for (int i = start; i < start + len; i++)
        {
            c = Integer.toHexString(buffer[i] & 0xFF);
            s.append(c.length() < 2 ? "0" + c : c);
        }
        return s.toString();
    }

}
