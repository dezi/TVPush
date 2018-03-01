package com.p2p.p2pcamera;

public class P2PAVFrame
{
    private static final String LOGTAG = P2PAVFrame.class.getSimpleName();

    public static final int FRAMEINFO_SIZE = 24;

    public static final int AUDIO_CHANNEL_MONO = 0;
    public static final int AUDIO_CHANNEL_STERO = 1;

    public static final int AUDIO_DATABITS_8 = 0;
    public static final int AUDIO_DATABITS_16 = 1;

    public static final int AUDIO_SAMPLE_8K = 0;
    public static final int AUDIO_SAMPLE_11K = 1;
    public static final int AUDIO_SAMPLE_12K = 2;
    public static final int AUDIO_SAMPLE_16K = 3;
    public static final int AUDIO_SAMPLE_22K = 4;
    public static final int AUDIO_SAMPLE_24K = 5;
    public static final int AUDIO_SAMPLE_32K = 6;
    public static final int AUDIO_SAMPLE_44K = 7;
    public static final int AUDIO_SAMPLE_48K = 8;

    public static final byte FRM_STATE_COMPLETE = 0;
    public static final byte FRM_STATE_INCOMPLETE = 1;
    public static final byte FRM_STATE_LOSED = 2;
    public static final byte FRM_STATE_UNKOWN = -1;

    public static final int IPC_FRAME_FLAG_PBFRAME = 0;
    public static final int IPC_FRAME_FLAG_IFRAME = 1;
    public static final int IPC_FRAME_FLAG_MD = 2;
    public static final int IPC_FRAME_FLAG_IO = 3;

    public static final short MEDIA_CODEC_UNKNOWN = 0;

    public static final short MEDIA_CODEC_VIDEO_H263 = 77;
    public static final short MEDIA_CODEC_VIDEO_H264 = 78;
    public static final short MEDIA_CODEC_VIDEO_MJPEG = 79;
    public static final short MEDIA_CODEC_VIDEO_MPEG4 = 76;
    public static final short MEDIA_CODEC_VIDEO_H265 = 81;

    public static final short MEDIA_CODEC_AUDIO_AAC = 138;
    public static final short MEDIA_CODEC_AUDIO_ADPCM = 139;
    public static final short MEDIA_CODEC_AUDIO_G726 = 143;
    public static final short MEDIA_CODEC_AUDIO_MP3 = 142;
    public static final short MEDIA_CODEC_AUDIO_PCM = 140;
    public static final short MEDIA_CODEC_AUDIO_SPEEX = 141;

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
        codec_id = P2PPacker.byteArrayToShort(data, 0, isBigEndian);
        flags = data[2];
        liveFlag = data[3];
        onlineNum = data[4];
        useCount = data[5];
        frmNo = P2PPacker.byteArrayToShort(data, 6, isBigEndian);
        videoWidth = P2PPacker.byteArrayToShort(data, 8, isBigEndian);
        videoHeight = P2PPacker.byteArrayToShort(data, 10,  isBigEndian);
        timestamp = P2PPacker.byteArrayToInt(data, 12, isBigEndian);
        isDay = data[16];
        cover_state = data[17];
        outloss = data[18];
        inloss = data[19];
        timestamp_ms = P2PPacker.byteArrayToInt(data, 20, isBigEndian);

        frmSize = size - FRAMEINFO_SIZE;
        frmData = new byte[frmSize];

        System.arraycopy(data, FRAMEINFO_SIZE, frmData, 0, frmSize);

        panState = new PanState(onlineNum);
    }

    public int getSamplerate()
    {
        switch (flags >>> 2)
        {
            case AUDIO_SAMPLE_8K:
                return 16000;
            case AUDIO_SAMPLE_11K:
                return 11025;
            case AUDIO_SAMPLE_12K:
                return 12000;
            case AUDIO_SAMPLE_16K:
                return 16000;
            case AUDIO_SAMPLE_22K:
                return 22050;
            case AUDIO_SAMPLE_24K:
                return 24000;
            case AUDIO_SAMPLE_32K:
                return 32000;
            case AUDIO_SAMPLE_44K:
                return 44100;
            case AUDIO_SAMPLE_48K:
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
            case MEDIA_CODEC_VIDEO_H265:
                return "VIDEO_H265";
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
        return codec_id;
    }

    public byte getFlags()
    {
        return flags;
    }

    public short getFrmNo()
    {
        return frmNo;
    }

    public int getFrmSize()
    {
        return frmSize;
    }

    public byte getInloss()
    {
        return inloss;
    }

    public byte getOnlineNum()
    {
        return onlineNum;
    }

    public byte getOutloss()
    {
        return outloss;
    }

    public int getTimeStamp()
    {
        return timestamp;
    }

    public int getTimestamp_ms()
    {
        return timestamp_ms;
    }

    public int getVideoHeight()
    {
        return videoHeight;
    }

    public int getVideoWidth()
    {
        return videoWidth;
    }

    public boolean isCovered()
    {
        return cover_state == 1;
    }

    public boolean isDay()
    {
        return isDay == 1;
    }

    public boolean isVideo()
    {
        switch (codec_id)
        {
            case MEDIA_CODEC_VIDEO_H263:
            case MEDIA_CODEC_VIDEO_H264:
            case MEDIA_CODEC_VIDEO_MJPEG:
            case MEDIA_CODEC_VIDEO_MPEG4:
                return true;
        }

        return false;
    }

    public boolean isAudio()
    {
        switch (codec_id)
        {
            case MEDIA_CODEC_AUDIO_AAC:
            case MEDIA_CODEC_AUDIO_ADPCM:
            case MEDIA_CODEC_AUDIO_G726:
            case MEDIA_CODEC_AUDIO_MP3:
            case MEDIA_CODEC_AUDIO_PCM:
            case MEDIA_CODEC_AUDIO_SPEEX:
                return true;
        }

        return false;
    }

    public boolean isIFrame()
    {
        return (flags & 1) == 1;
    }

    public String toFrameString()
    {
        String spec = getCodecName();

        if (isVideo())
        {
            spec += " " + (isIFrame() ? "I" : "P") + " [" + getVideoWidth() + "x" + getVideoHeight() + "]";
        }

        if (isAudio())
        {
            spec += " " + getSamplerate();
        }

        return spec + " #" + getFrmNo() + " " + getTimeStamp() + " " + getFrmSize();
    }

    public String toString()
    {
        return "codec_id=" + codec_id +
                " flags=" + flags +
                " liveFlag=" + liveFlag +
                " onlineNum=" + onlineNum +
                " useCount=" + useCount +
                " frmNo=" + frmNo +
                " videoWidth=" + videoWidth +
                " videoHeight=" + videoHeight +
                " timestamp=" + timestamp +
                " isDay=" + isDay +
                " cover_state=" + cover_state +
                " outloss=" + outloss +
                " inloss=" + inloss +
                " timestamp_ms=" + timestamp_ms +
                " frmSize=" + frmSize;
    }
}