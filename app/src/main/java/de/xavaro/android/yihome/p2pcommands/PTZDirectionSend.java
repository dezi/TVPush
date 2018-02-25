package de.xavaro.android.yihome.p2pcommands;

import de.xavaro.android.yihome.P2PCommandCodes;
import de.xavaro.android.yihome.P2PMessage;
import de.xavaro.android.yihome.P2PPacker;
import de.xavaro.android.yihome.P2PSession;

public class PTZDirectionSend
{
    public final static int DIRECTION_UP = 1;
    public final static int DIRECTION_DOWN = 2;
    public final static int DIRECTION_LEFT = 3;
    public final static int DIRECTION_RIGHT = 4;

    public int direction;
    public int speed;

    private P2PSession session;

    public PTZDirectionSend(P2PSession session, int direction, int speed)
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
        byte[] data = new byte[8];

        System.arraycopy(P2PPacker.intToByteArray(direction, session.isBigEndian), 0, data, 0, 4);
        System.arraycopy(P2PPacker.intToByteArray(speed, session.isBigEndian), 0, data, 4, 4);

        return data;
    }
}
