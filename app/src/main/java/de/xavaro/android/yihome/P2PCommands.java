package de.xavaro.android.yihome;

import android.util.Log;

public class P2PCommands
{
    private static final String LOGTAG = P2PCommands.class.getSimpleName();

    public static void sendPTZDirection(P2PSession session, int direction, int speed)
    {
        Log.d(LOGTAG, "sendPTZDirection: direction=" + direction + " speed=" + speed);

        P2PMessage p2PMessage = new P2PMessage(
                P2PCommandCodes.PTZ_DIRECTION_CTRL,
                AVIOCTRLDEFs.SMsgAVIoctrlPTZDireCTRL.parseContent(direction, speed, session.isBigEndian));

        session.packDatAndSend(p2PMessage);
    }
}
