package zz.top.cam;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import org.json.JSONObject;

import zz.top.p2p.camera.P2PCamera;

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

    public abstract FrameLayout createSurface(Context context);
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

    public abstract boolean startPTZDirection(int direction, int speed);
    public abstract boolean stopPTZDirection();
}
