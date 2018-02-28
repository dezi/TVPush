package de.xavaro.android.p2pcamera;

public class P2PAVFrame
{
    private static final String LOGTAG = P2PAVFrame.class.getSimpleName();

    public static final int AUDIO_CHANNEL_MONO = 0;
    public static final int AUDIO_CHANNEL_STERO = 1;
    public static final int AUDIO_DATABITS_16 = 1;
    public static final int AUDIO_DATABITS_8 = 0;
    public static final int AUDIO_SAMPLE_11K = 1;
    public static final int AUDIO_SAMPLE_12K = 2;
    public static final int AUDIO_SAMPLE_16K = 3;
    public static final int AUDIO_SAMPLE_22K = 4;
    public static final int AUDIO_SAMPLE_24K = 5;
    public static final int AUDIO_SAMPLE_32K = 6;
    public static final int AUDIO_SAMPLE_44K = 7;
    public static final int AUDIO_SAMPLE_48K = 8;
    public static final int AUDIO_SAMPLE_8K = 0;
    public static final int FRAMEINFO_SIZE = 24;
    public static final byte FRM_STATE_COMPLETE = (byte) 0;
    public static final byte FRM_STATE_INCOMPLETE = (byte) 1;
    public static final byte FRM_STATE_LOSED = (byte) 2;
    public static final byte FRM_STATE_UNKOWN = (byte) -1;
    public static final int IPC_FRAME_FLAG_IFRAME = 1;
    public static final int IPC_FRAME_FLAG_IO = 3;
    public static final int IPC_FRAME_FLAG_MD = 2;
    public static final int IPC_FRAME_FLAG_PBFRAME = 0;
    public static final int MEDIA_CODEC_AUDIO_AAC = 138;
    public static final int MEDIA_CODEC_AUDIO_ADPCM = 139;
    public static final int MEDIA_CODEC_AUDIO_G726 = 143;
    public static final int MEDIA_CODEC_AUDIO_MP3 = 142;
    public static final int MEDIA_CODEC_AUDIO_PCM = 140;
    public static final int MEDIA_CODEC_AUDIO_SPEEX = 141;
    public static final int MEDIA_CODEC_UNKNOWN = 0;
    public static final int MEDIA_CODEC_VIDEO_H263 = 77;
    public static final int MEDIA_CODEC_VIDEO_H264 = 78;
    public static final int MEDIA_CODEC_VIDEO_MJPEG = 79;
    public static final int MEDIA_CODEC_VIDEO_MPEG4 = 76;
    private short codec_id = (short) 0;
    private byte cover_state;
    private byte flags = (byte) -1;
    public byte[] frmData = null;
    private short frmNo = (short) -1;
    private int frmSize = 0;
    private byte frmState = (byte) 0;
    private byte inloss;
    private byte isDay = (byte) 1;
    public byte liveFlag;
    private long oldfrmNo;
    private byte onlineNum = (byte) 0;
    private byte outloss;
    public PanState panState = new PanState((byte) 0);
    private int timestamp = 0;
    private int timestamp_ms = 0;
    public byte useCount;
    private int videoHeight = 0;
    private int videoWidth = 0;

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

        public boolean isCruiseState()
        {
            return isState(CRUISE_STATE);
        }

        public boolean isMoveTrackState()
        {
            return isState(MOVETRACK_STATE);
        }

        public boolean isPanMoving()
        {
            return isState(MOVING_STATE);
        }

        public boolean isPanoramaCapturingState()
        {
            return isState(PANORAMA_CAPTURING_STATE);
        }

        public boolean isPresetState()
        {
            return isState(PRESET_STATE);
        }

        public boolean isXBorderState()
        {
            return isState(X_BORDER_STATE);
        }

        public boolean isYBorderState()
        {
            return isState(Y_BORDER_STATE);
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

        this.frmSize = size - 24;
        this.frmData = new byte[this.frmSize];
        System.arraycopy(data, 24, this.frmData, 0, this.frmSize);

        this.panState = new PanState(this.onlineNum);
    }

    public static int getSamplerate(byte b)
    {
        switch (b >>> 2)
        {
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
            default:
                return 16000;
        }
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

    public byte getFrmState()
    {
        return this.frmState;
    }

    public byte getInloss()
    {
        return this.inloss;
    }

    public long getOldfrmNo()
    {
        return this.oldfrmNo;
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
        return "AVFrame: "
                + (isIFrame() ? "I" : "P")
                + " [" + getVideoWidth() + "x" + getVideoHeight() + "]"
                + " #" + getFrmNo()
                + " " + getTimeStamp()
                + " " + getFrmSize()
                ;
    }

    public String toString()
    {
        StringBuffer stringBuffer = new StringBuffer();

        stringBuffer.append("codec_id:" + this.codec_id);
        stringBuffer.append(", flags:" + this.flags);
        stringBuffer.append(", liveFlag:" + this.liveFlag);
        stringBuffer.append(", onlineNum:" + this.onlineNum);
        stringBuffer.append(", useCount:" + this.useCount);
        stringBuffer.append(", frmNo:" + this.frmNo);
        stringBuffer.append(", videoWidth:" + this.videoWidth);
        stringBuffer.append(", videoHeight:" + this.videoHeight);
        stringBuffer.append(", timestamp:" + this.timestamp);
        stringBuffer.append(", isDay:" + this.isDay);
        stringBuffer.append(", cover_state:" + this.cover_state);
        stringBuffer.append(", outloss:" + this.outloss);
        stringBuffer.append(", inloss:" + this.inloss);
        stringBuffer.append(", timestamp_ms:" + this.timestamp_ms);
        stringBuffer.append(", frmSize:" + this.frmSize);

        return stringBuffer.toString();
    }
}