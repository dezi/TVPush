package de.xavaro.android.p2pcamera.p2pcommands;

import de.xavaro.android.p2pcamera.P2PCommandCodes;
import de.xavaro.android.p2pcamera.P2PMessage;
import de.xavaro.android.p2pcamera.P2PPacker;
import de.xavaro.android.p2pcamera.P2PSession;

public class StartRealtimeSend
{
    public final static byte RESOLUTION_PREVIEW = 0;
    public final static byte RESOLUTION_HIGH = 1;

    public byte usecount;
    public byte resolution;
    public byte unknown;

    private P2PSession session;

    public StartRealtimeSend(P2PSession session, byte usecount, byte resolution, byte unknown)
    {
        this.session = session;
        this.usecount = usecount;
        this.resolution = resolution;
        this.unknown = unknown;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(P2PCommandCodes.IPCAM_TNP_START_REALTIME, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        byte[] data = new byte[4];

        data[0] = usecount;
        data[1] = resolution;
        data[2] = unknown;

        return data;
    }
}
