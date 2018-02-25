package de.xavaro.android.yihome.p2pcommands;

import de.xavaro.android.yihome.P2PCommandCodes;
import de.xavaro.android.yihome.P2PMessage;
import de.xavaro.android.yihome.P2PPacker;
import de.xavaro.android.yihome.P2PSession;

public class ResolutionSend
{
    public final static int RESOLUTION_720P = 1;
    public final static int RESOLUTION_1080P = 2;
    public final static int RESOLUTION_SUPER1080P = 3;

    public int resolution;

    private P2PSession session;

    public ResolutionSend(P2PSession session, int resolution)
    {
        this.session = session;
        this.resolution = resolution;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(P2PCommandCodes.IPCAM_SET_RESOLUTION, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        byte[] data = new byte[8];

        System.arraycopy(P2PPacker.intToByteArray(resolution, session.isBigEndian), 0, data, 0, 4);
        System.arraycopy(P2PPacker.intToByteArray(1, session.isBigEndian), 0, data, 4, 4);

        return data;
    }

}
