package zz.top.cam;

import android.content.Context;

import zz.top.gls.GLSVideoView;

public abstract class Camera
{
    private static final String LOGTAG = Camera.class.getSimpleName();

    public final static int RESOLUTION_AUTO = 0;
    public final static int RESOLUTION_1080P = 1;
    public final static int RESOLUTION_720P = 2;
    public final static int RESOLUTION_4K = 3;

    public final static int PTZ_DIRECTION_UP = 1;
    public final static int PTZ_DIRECTION_DOWN = 2;
    public final static int PTZ_DIRECTION_LEFT = 3;
    public final static int PTZ_DIRECTION_RIGHT = 4;

    public abstract GLSVideoView createSurface(Context context);
    public abstract void registerSurface(GLSVideoView surface);
    public abstract void releaseSurface();

    public abstract boolean isOnline(String uuid);
    public abstract boolean attachCamera(String uuid);

    public abstract boolean connectCamera();
    public abstract boolean disconnectCamera();

    public abstract boolean setResolution(int resolution);

    public abstract boolean startRealtimeVideo();
    public abstract boolean stopRealtimeVideo();

    public abstract boolean startRealtimeAudio();
    public abstract boolean stopRealtimeAudio();

    public abstract boolean startFaceDetection(boolean demodraw);
    public abstract boolean stopFaceDetection();

    public abstract boolean startPTZDirection(int direction, int speed);
    public abstract boolean stopPTZDirection();
}
