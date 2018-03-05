package com.p2p.p2pcamera;

import android.util.Log;

import com.p2p.p2pcamera.p2pcommands.DayNightSend;
import com.p2p.p2pcamera.p2pcommands.DeviceInfoQuery;
import com.p2p.p2pcamera.p2pcommands.PTZControlStopSend;
import com.p2p.p2pcamera.p2pcommands.PTZDirectionSend;
import com.p2p.p2pcamera.p2pcommands.PTZHomeSend;
import com.p2p.p2pcamera.p2pcommands.PTZJumpSend;
import com.p2p.p2pcamera.p2pcommands.ResolutionQuery;
import com.p2p.p2pcamera.p2pcommands.ResolutionSend;
import com.p2p.p2pcamera.p2pcommands.StartAudioSend;
import com.p2p.p2pcamera.p2pcommands.StartVideoSend;
import com.p2p.p2pcamera.p2pcommands.StopAudioSend;
import com.p2p.p2pcamera.p2pcommands.StopVideoSend;

public class P2PCamera
{
    private static final String LOGTAG = P2PCamera.class.getSimpleName();

    public String targetUUID;
    public String targetId;

    private P2PSession session;

    public P2PCamera(String targetUUID, String targetId, String targetPw, P2PVideoGLVideoView surface)
    {
        this.targetUUID = targetUUID;

        this.targetId = targetId;

        session = new P2PSession(targetId, targetPw);

        session.surface = surface;

        initialize();
    }

    public void initialize()
    {
        //
        // 4881 IPCAM_SET_RESOLUTION         send(1, 0x1311, 00 00 00 01 00 00 00 01 )
        // 9029 IPCAM_TNP_START_REALTIME     send(1, 0x2345, 02 01 01 00 )
        //  816 IPCAM_DEVINFO_REQ            send(1, 0x0330, 00 00 00 00 )
        // 4864 IPCAM_UPDATE_CHECK_PHONE_REQ send(1, 0x1300, 00 00 00 00 )
        // 9031 IPCAM_TNP_EVENT_LIST_REQ     send(1, 0x2347, 00 00 00 00 07 E2 02 03 07 07 06 00 07 E2 03 05 02 07 06 00 00 00 00 00 )
        //
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
