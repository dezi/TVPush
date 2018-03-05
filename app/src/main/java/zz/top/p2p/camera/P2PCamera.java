package zz.top.p2p.camera;

import android.util.Log;

import zz.top.p2p.commands.DayNightSend;
import zz.top.p2p.commands.DeviceInfoQuery;
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
import zz.top.p2p.video.VideoGLVideoView;

public class P2PCamera
{
    private static final String LOGTAG = P2PCamera.class.getSimpleName();

    public String targetUUID;
    public String targetId;

    private P2PSession session;

    public P2PCamera(String targetUUID, String targetId, String targetPw, VideoGLVideoView surface)
    {
        this.targetUUID = targetUUID;

        this.targetId = targetId;

        session = new P2PSession(targetId, targetPw);

        session.videoView = surface;
    }

    public boolean isOnline()
    {
        return session.isOnline();
    }

    public boolean connectCamera()
    {
        boolean isOnline = session.isOnline();
        boolean isConnected = session.connect();

        Log.d(LOGTAG, "initialize: isOnline=" + isOnline);
        Log.d(LOGTAG, "initialize: connect=" + isConnected);

        return isConnected;
    }

    public boolean disconnectCamera()
    {
        return session.disconnect();
    }

    public P2PSession getSession()
    {
        return session;
    }

    //region Delegate section.

    public final static byte RESOLUTION_AUTO = 0;
    public final static byte RESOLUTION_1080P = 1;
    public final static byte RESOLUTION_720P = 2;
    public final static byte RESOLUTION_SUPER1080P = 3;

    public boolean resolutionSend(int resolution)
    {
        return (new ResolutionSend(session, resolution)).send();
    }

    public boolean startVideoSend(byte resolution)
    {
        return (new StartVideoSend(session, (byte) 1, resolution, (byte) 1)).send();
    }

    public boolean stopVideoSend()
    {
        return (new StopVideoSend(session)).send();
    }

    public boolean startAudioSend()
    {
        return (new StartAudioSend(session)).send();
    }

    public boolean stopAudioSend()
    {
        return (new StopAudioSend(session)).send();
    }

    public final static byte DAYNIGHTSEND_DAYNIGHT_AUTO = 1;
    public final static byte DAYNIGHTSEND_DAYNIGHT_OFF = 2;
    public final static byte DAYNIGHTSEND_DAYNIGHT_ON = 3;

    public boolean dayNightSend(int daynight)
    {
        return (new DayNightSend(session, daynight, 0,0)).send();
    }

    public final static byte PTZ_DIRECTION_UP = 1;
    public final static byte PTZ_DIRECTION_DOWN = 2;
    public final static byte PTZ_DIRECTION_LEFT = 3;
    public final static byte PTZ_DIRECTION_RIGHT = 4;

    public boolean ptzDirectionSend(int direction, int speed)
    {
        return (new PTZDirectionSend(session, direction, speed)).send();
    }

    public boolean ptzControlStopSend()
    {
        return (new PTZControlStopSend(session)).send();
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

    //endregion Delegate section.
}
