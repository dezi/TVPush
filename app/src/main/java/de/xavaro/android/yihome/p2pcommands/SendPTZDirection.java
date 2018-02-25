package de.xavaro.android.yihome.p2pcommands;

import android.util.Log;

import de.xavaro.android.yihome.P2PCommandCodes;
import de.xavaro.android.yihome.P2PMessage;
import de.xavaro.android.yihome.P2PPacket;
import de.xavaro.android.yihome.P2PSession;

public class SendPTZDirection
{
    public int direction;
    public int speed;

    private P2PSession session;

    public SendPTZDirection(P2PSession session, int direction, int speed)
    {
        this.session = session;
        this.direction = direction;
        this.speed = speed;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(P2PCommandCodes.PTZ_DIRECTION_CTRL, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        byte[] obj = new byte[8];

        System.arraycopy(P2PPacket.intToByteArray(direction, session.isBigEndian), 0, obj, 0, 4);
        System.arraycopy(P2PPacket.intToByteArray(speed, session.isBigEndian), 0, obj, 4, 4);

        return obj;
    }
}
