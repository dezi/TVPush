package com.p2p.p2pcamera;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.util.Log;

import com.aac.utils.DecodeAAC;

import java.nio.ByteBuffer;

public class P2PReaderThreadAudio extends P2PReaderThread
{
    private static final String LOGTAG = P2PReaderThreadAudio.class.getSimpleName();

    private MediaCodec decoder;
    private MediaFormat format;
    private byte[] decodeData;

    public P2PReaderThreadAudio(P2PSession session, byte channel)
    {
        super(session, channel);
    }

    public boolean onStart()
    {
        decodeData = new byte[ 10 * 1024 ];

        DecodeAAC.nOpen();

        try
        {
            format = MediaFormat.createAudioFormat("audio/mp4a-latm", 32000, 1);

            int profile = MediaCodecInfo.CodecProfileLevel.AACObjectLC;
            int srate = 5;
            int channel = 1;

            ByteBuffer csd = ByteBuffer.allocate(2);
            csd.put(0, (byte)(profile << 3 | srate >> 1));
            csd.put(1, (byte)((srate & 0x01) << 7 | channel << 3));

            format.setByteBuffer("csd-0", csd);

            decoder = MediaCodec.createDecoderByType("audio/mp4a-latm");
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
        DecodeAAC.nOpen();

        decodeData = null;

        decoder.stop();
        decoder.release();
        decoder = null;

        return true;
    }

    @Override
    public boolean handleData(byte[] data, int size)
    {
        P2PAVFrame aVFrame = new P2PAVFrame(data, size, session.isBigEndian);

        int nDecode = DecodeAAC.nDecode(aVFrame.frmData, aVFrame.getFrmSize(), this.decodeData, this.decodeData.length);

        if ((aVFrame.getFrmNo() % 30) == 0)
        {
            Log.d(LOGTAG, "handleData: " + aVFrame.toFrameString() + " decoded=" + nDecode);
        }

        int inIndex = decoder.dequeueInputBuffer(1000);

        if (inIndex >= 0)
        {
            //Log.d(LOGTAG, "dequeueInputBuffer: inx=" + inIndex);
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

        MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();

        int outIndex = decoder.dequeueOutputBuffer(info, 1000);

        switch (outIndex)
        {
            case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                MediaFormat format = decoder.getOutputFormat();
                Log.d(LOGTAG, "New format " + format);
                break;

            case MediaCodec.INFO_TRY_AGAIN_LATER:
                //Log.d(LOGTAG, "dequeueOutputBuffer timed out!");
                break;

            default:
                ByteBuffer outBuffer = decoder.getOutputBuffer(inIndex);
                Log.v(LOGTAG, "############## " + outBuffer);
                decoder.releaseOutputBuffer(outIndex, false);
                break;
        }

        return true;
    }

    private MediaFormat makeAACCodecSpecificData(int audioProfile, int sampleRate, int channelConfig)
    {
        MediaFormat format = new MediaFormat();

        format.setString(MediaFormat.KEY_MIME, "audio/mp4a-latm");
        format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRate);
        format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, channelConfig);

        // 0000 0000 0001 0010 - 0000 0000 0001 0010 => 44100 stereo
        //                  10   0

        int samplingFreq[] =
                {
                        96000, 88200, 64000, 48000, 44100, 32000, 24000, 22050,
                        16000, 12000, 11025, 8000
                };

        int profile = 2;
        int srate = 5;
        int channel = 1;

        ByteBuffer csd = ByteBuffer.allocate(2);
        csd.put(0, (byte)(profile << 3 | srate >> 1));
        csd.put(1, (byte)((srate & 0x01) << 7 | channel << 3));

        format.setByteBuffer("csd-0", csd);

        return format;
    }

}
