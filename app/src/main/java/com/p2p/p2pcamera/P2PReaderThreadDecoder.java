package com.p2p.p2pcamera;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.util.Log;

import java.nio.ByteBuffer;

public class P2PReaderThreadDecoder extends Thread
{
    private static final String LOGTAG = P2PReaderThreadDecoder.class.getSimpleName();

    private P2PSession session;
    private MediaCodec decoder;

    private byte[] yuvbuf;
    private boolean haveIFrame;

    public P2PReaderThreadDecoder(P2PSession session)
    {
        super();

        this.session = session;
    }

    @Override
    public void run()
    {
        Log.d(LOGTAG, "run: start.");

        onStart();

        while (session.isConnected)
        {
            P2PAVFrame avFrame = null;

            synchronized (session.decodeFrames)
            {
                if (session.decodeFrames.size() > 0)
                {
                    avFrame = session.decodeFrames.remove(0);
                }
            }

            if (avFrame != null)
            {
                handleData(avFrame);
            }
            else
            {
                try
                {
                    Thread.sleep(10);
                }
                catch (Exception ignore)
                {
                }
            }
        }

        onStop();

        Log.d(LOGTAG, "run: stop.");
    }

    public boolean onStart()
    {
        yuvbuf = new byte[ 4000000 ];

        try
        {
            //selectVideoCodec("video/avc");

            MediaFormat format = MediaFormat.createVideoFormat("video/avc", 640, 360);

            /*
            Log.d(LOGTAG, "Vorher format=" + format);

            format.setInteger("bitrate", 500000);
            format.setInteger("frame-rate", 15);
            //format.setInteger("color-format", 19);
            format.setInteger("i-frame-interval", 5);
            format.setInteger("profile", 8);
            format.setInteger("level", 512);

            Log.d(LOGTAG, "Nachher format=" + format);
            */

            //format={csd-1=java.nio.HeapByteBuffer[pos=0 lim=8 cap=8],
            // track-id=1, height=320, profile=8, durationUs=2169921000,
            // mime=video/avc, frame-rate=15, display-height=320, width=480,
            // max-input-size=1572864, csd-0=java.nio.HeapByteBuffer[pos=0 lim=30 cap=30],
            // display-width=480, level=512}

            //Nachher format={width=640, height=360, bitrate=500000, mime=video/avc, frame-rate=15, i-frame-interval=5, color-format=19}

            //PlayerThread:{crop-top=0, crop-right=479, color-format=2141391876,
            // height=320, max_capacity=3010560, color-standard=4, crop-left=0,
            // color-transfer=3, stride=512, mime=video/raw, slice-height=320,
            // remained_resource=2856960, width=480, color-range=2, crop-bottom=319}

            //
            //dequeue format={crop-top=0, crop-right=639, color-format=2141391876,
            // height=368, max_capacity=3010560, color-standard=4, crop-left=0,
            // color-transfer=3, stride=640, mime=video/raw, slice-height=384,
            // remained_resource=2780160, width=640, color-range=2, crop-bottom=359}

            //decoder = MediaCodec.createByCodecName("OMX.google.h264.decoder");
            //decoder = MediaCodec.createByCodecName("OMX.SEC.avc.sw.dec");
            decoder = MediaCodec.createDecoderByType("video/avc");
            decoder.configure(format, null, null, 0);
            decoder.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        Log.d(LOGTAG, "onStart: done.");

        return true;
    }

    public boolean onStop()
    {
        decoder.stop();
        decoder.release();
        decoder = null;

        yuvbuf = null;

        Log.d(LOGTAG, "onStop: done.");

        return true;
    }

    public boolean handleData(P2PAVFrame aVFrame)
    {
        try
        {
            if ((decoder != null) && aVFrame.isIFrame() || haveIFrame)
            {
                haveIFrame = true;

                int inIndex = decoder.dequeueInputBuffer(1000);

                if (inIndex >= 0)
                {
                    ByteBuffer buffer = decoder.getInputBuffer(inIndex);

                    if (buffer != null)
                    {
                        buffer.clear();
                        buffer.put(aVFrame.frmData, 0, aVFrame.frmData.length);
                    }

                    decoder.queueInputBuffer(inIndex, 0, aVFrame.frmData.length, 0, 0);
                }
                else
                {
                    Log.d(LOGTAG, "dequeueInputBuffer: nix");
                }

                MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                int outIndex = decoder.dequeueOutputBuffer(bufferInfo, 1000);

                if (outIndex < 0)
                {
                    Log.v(LOGTAG, "############## " + outIndex);
                }
                else
                {
                    ByteBuffer buffer = decoder.getOutputBuffer(outIndex);

                    if (buffer != null)
                    {
                        MediaFormat format = decoder.getOutputFormat();

                        int width = format.getInteger("crop-right") + 1;
                        int height = format.getInteger("crop-bottom") + 1;

                        Log.d(LOGTAG, "handleData dequeue"
                                + " offset=" + bufferInfo.offset
                                + " size=" + bufferInfo.size
                                + " width=" + width
                                + " height=" + height
                        );

                        yuvbuf = new byte[bufferInfo.size];
                        buffer.get(yuvbuf);

                        session.surface.mRenderer.setFrame(yuvbuf, width, height);
                    }

                    decoder.releaseOutputBuffer(outIndex, false);
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            decoder = null;
        }

        return true;
    }

    protected static final MediaCodecInfo selectVideoCodec(final String mimeType)
    {
        Log.v(LOGTAG, "selectVideoCodec:");

        final int numCodecs = MediaCodecList.getCodecCount();

        for (int i = 0; i < numCodecs; i++)
        {
            final MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(i);

            if (codecInfo.isEncoder())
            {
                continue;
            }

            Log.d(LOGTAG, "selectVideoCodec name=" + codecInfo.getName());


            final String[] types = codecInfo.getSupportedTypes();
            for (int j = 0; j < types.length; j++)
            {
                if (types[j].equalsIgnoreCase(mimeType))
                {
                    Log.i(LOGTAG, "selectVideoCodec:" + codecInfo.getName() + " MIME=" + types[j]);
                    final int format = selectColorFormat(codecInfo, mimeType);

                    if (format > 0)
                    {
                        Log.d(LOGTAG, "selectVideoCodec match=" + codecInfo.getName());
                    }
                }
            }
        }
        return null;
    }

    protected static final int selectColorFormat(final MediaCodecInfo codecInfo, final String mimeType)
    {
        Log.i(LOGTAG, "selectColorFormat: ");

        int result = 0;
        final MediaCodecInfo.CodecCapabilities caps;
        try
        {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            caps = codecInfo.getCapabilitiesForType(mimeType);
        }
        finally
        {
            Thread.currentThread().setPriority(Thread.NORM_PRIORITY);
        }
        int colorFormat;
        for (int i = 0; i < caps.colorFormats.length; i++)
        {
            colorFormat = caps.colorFormats[i];

            Log.d(LOGTAG, "selectVideoCodec cformat=" + Integer.toHexString(colorFormat));

            if (isRecognizedViewoFormat(colorFormat))
            {
                if (result == 0)
                    result = colorFormat;
                break;
            }
        }
        if (result == 0)
            Log.e(LOGTAG, "couldn't find a good color format for " + codecInfo.getName() + " / " + mimeType);
        return result;
    }

    protected static int[] recognizedFormats;
    static {
        recognizedFormats = new int[] {
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
//        	MediaCodecInfo.CodecCapabilities.COLOR_QCOM_FormatYUV420SemiPlanar,
                MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface,
        };
    }

    private static final boolean isRecognizedViewoFormat(final int colorFormat)
    {
        Log.i(LOGTAG, "isRecognizedViewoFormat:colorFormat=" + colorFormat);
        final int n = recognizedFormats != null ? recognizedFormats.length : 0;
        for (int i = 0; i < n; i++)
        {
            if (recognizedFormats[i] == colorFormat)
            {
                return true;
            }
        }
        return false;
    }
}

