package zz.top.cam;

public abstract class Camera
{
    private static final String LOGTAG = Camera.class.getSimpleName();

    public abstract boolean connectCamera();
    public abstract boolean disconnectCamera();

    public abstract boolean setResolution(int resolution);
    public abstract boolean setPTZ(int pan, int tilt, int zoom);

    public abstract boolean startRealtimeVideo();
    public abstract boolean stopRealtimeVideo();

    public abstract boolean startRealtimeAudio();
    public abstract boolean stopRealtimeAudio();
}
