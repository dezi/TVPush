package zz.top.p2p.camera;

import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;

import org.json.JSONObject;

import pub.android.interfaces.drv.Camera;

import zz.top.p2p.commands.CloseCameraSend;
import zz.top.p2p.commands.DayNightSend;
import zz.top.p2p.commands.DeviceInfoQuery;
import zz.top.p2p.commands.LEDOnOffSend;
import zz.top.p2p.commands.PTZControlStopSend;
import zz.top.p2p.commands.PTZDirectionSend;
import zz.top.p2p.commands.PTZHomeSend;
import zz.top.p2p.commands.PTZJumpSend;
import zz.top.p2p.commands.ResolutionQuery;
import zz.top.p2p.commands.ResolutionSend;
import zz.top.p2p.commands.StartAudioSend;
import zz.top.p2p.commands.StartVideoSend;
import zz.top.p2p.commands.StopAudioSend;
import zz.top.p2p.commands.StopVideoSend;

import zz.top.gls.GLSVideoView;

import zz.top.cam.Cameras;
import zz.top.utl.Json;
import zz.top.utl.Simple;

@SuppressWarnings({"WeakerAccess", "unused"})
public class P2PCamera implements Camera
{
    private static final String LOGTAG = P2PCamera.class.getSimpleName();

    public final static byte RESOLUTION_AUTO = 0;
    public final static byte RESOLUTION_1080P = 1;
    public final static byte RESOLUTION_720P = 2;
    public final static byte RESOLUTION_SUPER1080P = 3;

    public final static byte DAYNIGHTSEND_DAYNIGHT_AUTO = 1;
    public final static byte DAYNIGHTSEND_DAYNIGHT_OFF = 2;
    public final static byte DAYNIGHTSEND_DAYNIGHT_ON = 3;

    public final static byte PTZ_DIRECTION_UP = 1;
    public final static byte PTZ_DIRECTION_DOWN = 2;
    public final static byte PTZ_DIRECTION_LEFT = 3;
    public final static byte PTZ_DIRECTION_RIGHT = 4;

    private final P2PSession session;

    private int resolution = RESOLUTION_AUTO;

    public P2PCamera()
    {
        session = new P2PSession();
    }

    //region Interface.

    @Override
    public FrameLayout createSurface(Context context)
    {
        return new GLSVideoView(context);
    }

    @Override
    public void registerSurface(FrameLayout videoView)
    {
        session.setVideoView(videoView);
    }

    @Override
    public void releaseSurface()
    {
        session.setVideoView(null);
    }

    @Override
    public boolean isOnline(JSONObject device, JSONObject credentials)
    {
        if (device != null)
        {
            String p2p_id = Json.getString(device, "p2p_id");

            return session.isOnline(p2p_id);
        }

        return false;
    }

    @Override
    public boolean attachCamera(JSONObject device, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String p2p_id = Json.getString(credentials, "p2p_id");
        String p2p_pw = Json.getString(credentials, "p2p_pw");

        if ((p2p_id != null) && (p2p_pw != null))
        {
            session.attachCamera(uuid, p2p_id, p2p_pw);

            return true;
        }

        return false;
    }

    @Override
    public boolean connectCamera()
    {
        boolean isConnected = session.connect();

        Log.d(LOGTAG, "initialize: connect=" + isConnected);

        return isConnected;
    }

    @Override
    public boolean disconnectCamera()
    {
        return session.disconnect();
    }

    @Override
    public boolean startRealtimeVideo()
    {
        session.startVideoThreads();

        return (new StartVideoSend(session, (byte) 2, (byte) resolution, (byte) 1)).send();
    }

    @Override
    public boolean stopRealtimeVideo()
    {
        Simple.getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                session.stopVideoThreads();
            }
        });

        return (new StopVideoSend(session)).send();
    }

    @Override
    public boolean startRealtimeAudio()
    {
        session.startAudioThreads();

        return (new StartAudioSend(session)).send();
    }

    @Override
    public boolean stopRealtimeAudio()
    {
        Simple.getHandler().post(new Runnable()
        {
            @Override
            public void run()
            {
                session.stopAudioThreads();
            }
        });

        return (new StopAudioSend(session)).send();
    }

    @Override
    public boolean startFaceDetection(boolean demodraw)
    {
        session.getGLSVideoView().setFaceDetecion(true);
        session.getGLSVideoView().setFaceDetecionDraw(demodraw);

        return true;
    }

    @Override
    public boolean stopFaceDetection()
    {
        session.getGLSVideoView().setFaceDetecion(false);

        return true;
    }

    @Override
    public boolean setResolution(int resolution)
    {
        if (resolution == RESOLUTION_4K)
        {
            resolution = RESOLUTION_1080P;
        }

        this.resolution = resolution;

        return (new ResolutionSend(session, resolution)).send();
    }

    @Override
    public boolean setCameraClosed(boolean closed)
    {
        return (new CloseCameraSend(session, closed)).send();
    }

    @Override
    public boolean setLEDOnOff(boolean onoff)
    {
        return (new LEDOnOffSend(session, onoff)).send();
    }

    @Override
    public boolean startPTZDirection(int direction, int speed)
    {
        return (new PTZDirectionSend(session, direction, speed)).send();
    }

    @Override
    public boolean stopPTZDirection()
    {
        return (new PTZControlStopSend(session)).send();
    }

    //endregion Interface.

    //endregion Additionals.

    public P2PSession getSession()
    {
        return session;
    }

    public boolean dayNightSend(int daynight)
    {
        return (new DayNightSend(session, daynight, 0,0)).send();
    }

    public boolean ptzHomeSend()
    {
        return (new PTZHomeSend(session)).send();
    }

    public boolean ptzJumpSend(int transverseProportion, int longitudinalProportion)
    {
        return (new PTZJumpSend(session, transverseProportion, longitudinalProportion)).send();
    }

    public boolean resolutionQuery()
    {
        return (new ResolutionQuery(session)).send();
    }

    public boolean deviceInfoQuery()
    {
        return (new DeviceInfoQuery(session)).send();
    }

    //endregion Additionals.
}
