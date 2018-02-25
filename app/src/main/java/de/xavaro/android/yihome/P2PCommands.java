package de.xavaro.android.yihome;

import android.util.Log;

public class P2PCommands
{
    private static final String LOGTAG = P2PCommands.class.getSimpleName();

    public static byte[] sendPTZDirection(int session, int direction, int speed)
    {
        Log.d(LOGTAG, "sendPTZDirection: direction=" + direction + " speed=" + speed);

        /*
        P2PMessage p2PMessage = new P2PMessage(
                P2PCommandCodes.PTZ_DIRECTION_CTRL,
                AVIOCTRLDEFs.SMsgAVIoctrlPTZDireCTRL.parseContent(direction, speed, isByteOrderBig));

        Log.d(LOGTAG, "sendPanDirection: command=" + Integer.toHexString(p2PMessage.reqId));
        Log.d(LOGTAG, "sendPanDirection: p2pmess=" + Simple.getHexBytesToString(p2PMessage.data));

        packDatAndSend(session, p2PMessage);
        */

        return new byte[1];
    }
}
