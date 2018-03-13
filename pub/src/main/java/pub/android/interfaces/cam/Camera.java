package pub.android.interfaces.cam;

import android.content.Context;
import android.widget.FrameLayout;

@SuppressWarnings("unused")
public interface Camera
{
     int RESOLUTION_AUTO = 0;
     int RESOLUTION_1080P = 1;
     int RESOLUTION_720P = 2;
     int RESOLUTION_4K = 3;

     int PTZ_DIRECTION_UP = 1;
     int PTZ_DIRECTION_DOWN = 2;
     int PTZ_DIRECTION_LEFT = 3;
     int PTZ_DIRECTION_RIGHT = 4;

     FrameLayout createSurface(Context context);
     void registerSurface(FrameLayout surface);
     void releaseSurface();

     boolean isOnline(String uuid);
     boolean attachCamera(String uuid);

     boolean connectCamera();
     boolean disconnectCamera();

     boolean setResolution(int resolution);

     boolean setCameraClosed(boolean closed);
     boolean setLEDOnOff(boolean onoff);

     boolean startRealtimeVideo();
     boolean stopRealtimeVideo();

     boolean startRealtimeAudio();
     boolean stopRealtimeAudio();

     boolean startFaceDetection(boolean demodraw);
     boolean stopFaceDetection();

     boolean startPTZDirection(int direction, int speed);
     boolean stopPTZDirection();
}
