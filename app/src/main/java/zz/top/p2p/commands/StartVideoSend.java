package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PMessage;
import zz.top.p2p.camera.P2PSession;

public class StartVideoSend
{
    public final static byte RESOLUTION_AUTO = 0;
    public final static byte RESOLUTION_1080P = 1;
    public final static byte RESOLUTION_720P = 2;
    public final static byte RESOLUTION_SUPER1080P = 3;

    public byte usecount;
    public byte resolution;
    public byte unknown;

    private P2PSession session;

    public StartVideoSend(P2PSession session, byte usecount, byte resolution, byte unknown)
    {
        this.session = session;
        this.usecount = usecount;
        this.resolution = resolution;
        this.unknown = unknown;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.IPCAM_TNP_START_REALTIME, build());

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
