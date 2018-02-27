package de.xavaro.android.yihome;

import android.util.Log;

import de.xavaro.android.yihome.p2pcommands.DeviceInfoQuery;
import de.xavaro.android.yihome.p2pcommands.PTZControlStopSend;
import de.xavaro.android.yihome.p2pcommands.PTZDirectionSend;
import de.xavaro.android.yihome.p2pcommands.PTZHomeSend;
import de.xavaro.android.yihome.p2pcommands.PTZJumpSend;
import de.xavaro.android.yihome.p2pcommands.ResolutionQuery;
import de.xavaro.android.yihome.p2pcommands.ResolutionSend;

public class P2PCamera
{
    private static final String LOGTAG = P2PCamera.class.getSimpleName();

    public String targetId;
    private P2PSession session;

    public P2PCamera(String targetId, String targetPw)
    {
        this.targetId = targetId;

        session = new P2PSession(targetId, targetPw);
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

    public boolean resolutionSend(int resolution)
    {
        return (new ResolutionSend(session, resolution)).send();
    }

    public boolean resolutionQuery()
    {
        return (new ResolutionQuery(session)).send();
    }

    public boolean ptzDirectionSend(int direction, int speed)
    {
        return (new PTZDirectionSend(session, direction, speed)).send();
    }

    public boolean ptzJumpSend(int transverseProportion, int longitudinalProportion)
    {
        return (new PTZJumpSend(session, transverseProportion, longitudinalProportion)).send();
    }

    public boolean ptzControlStopSend()
    {
        return (new PTZControlStopSend(session)).send();
    }

    public boolean ptzHomeSend()
    {
        return (new PTZHomeSend(session)).send();
    }

    public boolean deviceInfoquery()
    {
        return (new DeviceInfoQuery(session)).send();
    }

    //endregion Delegate section.
}
