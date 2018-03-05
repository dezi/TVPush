package zz.top.cam;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

import org.json.JSONObject;

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

    @Nullable
    public Camera createCameraByName(String name)
    {
        return createCameraByUUID(Cameras.findCameraByName(name));
    }

    @Nullable
    public Camera createCameraByNick(String nick)
    {
        return createCameraByUUID(Cameras.findCameraByNick(nick));
    }

    @Nullable
    public Camera createCameraByDeviceID(String deviceID)
    {
        return createCameraByUUID(Cameras.findCameraByDeviceID(deviceID));
    }

    @Nullable
    public Camera createCameraByUUID(String uuid)
    {
        JSONObject device = Cameras.getCameraDevice(uuid);

        return null;
    }

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
