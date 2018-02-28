package com.p2p.p2pcamera;

public class P2PAVFrame
{
    private static final String LOGTAG = P2PAVFrame.class.getSimpleName();

    public static final int FRAMEINFO_SIZE = 24;

    public static final int AUDIO_CHANNEL_MONO = 0;
    public static final int AUDIO_CHANNEL_STERO = 1;

    public static final int AUDIO_DATABITS_16 = 1;
    public static final int AUDIO_DATABITS_8 = 0;

    public static final int AUDIO_SAMPLE_8K = 0;
    public static final int AUDIO_SAMPLE_11K = 1;
    public static final int AUDIO_SAMPLE_12K = 2;
    public static final int AUDIO_SAMPLE_16K = 3;
    public static final int AUDIO_SAMPLE_22K = 4;
    public static final int AUDIO_SAMPLE_24K = 5;
    public static final int AUDIO_SAMPLE_32K = 6;
    public static final int AUDIO_SAMPLE_44K = 7;
    public static final int AUDIO_SAMPLE_48K = 8;

    public static final byte FRM_STATE_COMPLETE = (byte) 0;
    public static final byte FRM_STATE_INCOMPLETE = (byte) 1;
    public static final byte FRM_STATE_LOSED = (byte) 2;
    public static final byte FRM_STATE_UNKOWN = (byte) -1;

    public static final int IPC_FRAME_FLAG_IFRAME = 1;
    public static final int IPC_FRAME_FLAG_IO = 3;
    public static final int IPC_FRAME_FLAG_MD = 2;
    public static final int IPC_FRAME_FLAG_PBFRAME = 0;

    public static final int MEDIA_CODEC_UNKNOWN = 0;

    public static final int MEDIA_CODEC_VIDEO_H263 = 77;
    public static final int MEDIA_CODEC_VIDEO_H264 = 78;
    public static final int MEDIA_CODEC_VIDEO_MJPEG = 79;
    public static final int MEDIA_CODEC_VIDEO_MPEG4 = 76;

    public static final int MEDIA_CODEC_AUDIO_AAC = 138;
    public static final int MEDIA_CODEC_AUDIO_ADPCM = 139;
    public static final int MEDIA_CODEC_AUDIO_G726 = 143;
    public static final int MEDIA_CODEC_AUDIO_MP3 = 142;
    public static final int MEDIA_CODEC_AUDIO_PCM = 140;
    public static final int MEDIA_CODEC_AUDIO_SPEEX = 141;

    private short codec_id;
    private byte cover_state;
    private byte flags;
    private short frmNo;
    private int frmSize;
    private byte inloss;
    private byte isDay;
    private byte liveFlag;
    private byte onlineNum;
    private byte outloss;
    private int timestamp;
    private int timestamp_ms;
    private byte useCount;
    private int videoHeight;
    private int videoWidth;

    public byte[] frmData;
    public PanState panState;

    public static class PanState
    {
        public static final byte PRESET_STATE = (byte) 1;
        public static final byte MOVETRACK_STATE = (byte) 2;
        public static final byte CRUISE_STATE = (byte) 4;
        public static final byte PANORAMA_CAPTURING_STATE = (byte) 8;
        public static final byte Y_BORDER_STATE = (byte) 16;
        public static final byte X_BORDER_STATE = (byte) 32;
        public static final byte MOVING_STATE = (byte) 255;

        private byte state;

        public PanState(byte state)
        {
            this.state = state;
        }

        private boolean isState(byte b)
        {
            return (state & b) == b;
        }

        public boolean isPresetState()
        {
            return isState(PRESET_STATE);
        }

        public boolean isMoveTrackState()
        {
            return isState(MOVETRACK_STATE);
        }

        public boolean isCruiseState()
        {
            return isState(CRUISE_STATE);
        }

        public boolean isPanoramaCapturingState()
        {
            return isState(PANORAMA_CAPTURING_STATE);
        }

        public boolean isYBorderState()
        {
            return isState(Y_BORDER_STATE);
        }

        public boolean isXBorderState()
        {
            return isState(X_BORDER_STATE);
        }

        public boolean isPanMoving()
        {
            return isState(MOVING_STATE);
        }
    }

    public P2PAVFrame(byte[] data, int size, boolean isBigEndian)
    {
        this.codec_id = P2PPacker.byteArrayToShort(data, 0, isBigEndian);
        this.flags = data[2];
        this.liveFlag = data[3];
        this.onlineNum = data[4];
        this.useCount = data[5];
        this.frmNo = P2PPacker.byteArrayToShort(data, 6, isBigEndian);
        this.videoWidth = P2PPacker.byteArrayToShort(data, 8, isBigEndian);
        this.videoHeight = P2PPacker.byteArrayToShort(data, 10,  isBigEndian);
        this.timestamp = P2PPacker.byteArrayToInt(data, 12, isBigEndian);
        this.isDay = data[16];
        this.cover_state = data[17];
        this.outloss = data[18];
        this.inloss = data[19];
        this.timestamp_ms = P2PPacker.byteArrayToInt(data, 20, isBigEndian);

        this.frmSize = size - FRAMEINFO_SIZE;
        this.frmData = new byte[this.frmSize];

        System.arraycopy(data, 24, this.frmData, 0, this.frmSize);

        this.panState = new PanState(this.onlineNum);
    }

    public int getSamplerate()
    {
        switch (flags >>> 2)
        {
            case 0:
                return 16000;
            case 1:
                return 11025;
            case 2:
                return 12000;
            case 4:
                return 22050;
            case 5:
                return 24000;
            case 6:
                return 32000;
            case 7:
                return 44100;
            case 8:
                return 48000;
        }

        return 16000;
    }

    public String getCodecName()
    {
        switch (codec_id)
        {
            case MEDIA_CODEC_UNKNOWN:
                return "UNKNOWN";
            case MEDIA_CODEC_VIDEO_H263:
                return "VIDEO_H263";
            case MEDIA_CODEC_VIDEO_H264:
                return "VIDEO_H264";
            case MEDIA_CODEC_VIDEO_MJPEG:
                return "VIDEO_MJPEG";
            case MEDIA_CODEC_VIDEO_MPEG4:
                return "VIDEO_MPEG4";
            case MEDIA_CODEC_AUDIO_AAC:
                return "AUDIO_AAC";
            case MEDIA_CODEC_AUDIO_ADPCM:
                return "AUDIO_ADPCM";
            case MEDIA_CODEC_AUDIO_G726:
                return "AUDIO_G726";
            case MEDIA_CODEC_AUDIO_MP3:
                return "AUDIO_MP3";
            case MEDIA_CODEC_AUDIO_PCM:
                return "AUDIO_PCM";
            case MEDIA_CODEC_AUDIO_SPEEX:
                return "AUDIO_SPEEX";
        }

        return "UNKNOWN";
    }

    public short getCodecId()
    {
        return this.codec_id;
    }

    public byte getFlags()
    {
        return this.flags;
    }

    public short getFrmNo()
    {
        return this.frmNo;
    }

    public int getFrmSize()
    {
        return this.frmSize;
    }

    public byte getInloss()
    {
        return this.inloss;
    }

    public byte getOnlineNum()
    {
        return this.onlineNum;
    }

    public byte getOutloss()
    {
        return this.outloss;
    }

    public int getTimeStamp()
    {
        return this.timestamp;
    }

    public int getTimestamp_ms()
    {
        return this.timestamp_ms;
    }

    public int getVideoHeight()
    {
        return this.videoHeight;
    }

    public int getVideoWidth()
    {
        return this.videoWidth;
    }

    public boolean isCovered()
    {
        return this.cover_state == 1;
    }

    public boolean isDay()
    {
        return this.isDay == 1;
    }

    public boolean isIFrame()
    {
        return (this.flags & 1) == 1;
    }

    public void setInloss(byte b)
    {
        this.inloss = b;
    }

    public void setOutloss(byte b)
    {
        this.outloss = b;
    }

    public void setTimestamp(int i)
    {
        this.timestamp = i;
    }

    public void setTimestamp_ms(int i)
    {
        this.timestamp_ms = i;
    }

    public String toFrameString()
    {
        return (isIFrame() ? "I" : "P")
                + " [" + getVideoWidth() + "x" + getVideoHeight() + "]"
                + " #" + getFrmNo()
                + " " + getTimeStamp()
                + " " + getFrmSize()
                ;
    }

    public String toString()
    {
        return "codec_id=" + this.codec_id +
                " flags=" + this.flags +
                " liveFlag=" + this.liveFlag +
                " onlineNum=" + this.onlineNum +
                " useCount=" + this.useCount +
                " frmNo=" + this.frmNo +
                " videoWidth=" + this.videoWidth +
                " videoHeight=" + this.videoHeight +
                " timestamp=" + this.timestamp +
                " isDay=" + this.isDay +
                " cover_state=" + this.cover_state +
                " outloss=" + this.outloss +
                " inloss=" + this.inloss +
                " timestamp_ms=" + this.timestamp_ms +
                " frmSize=" + this.frmSize;
    }
}