package zz.top.gls;

public abstract class GLSFrame
{
    public abstract boolean isVideo();
    public abstract boolean isAudio();
    public abstract boolean isIFrame();

    public abstract int getCodecId();
    public abstract String getCodecName();

    public abstract int getFrameNo();
    public abstract int getFrameSize();
    public abstract byte[] getFrameData();

    public abstract int getVideoHeight();
    public abstract int getVideoWidth();

    public abstract long getTimeStamp();
}
