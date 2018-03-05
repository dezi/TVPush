package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PMessage;
import zz.top.p2p.camera.P2PPacker;
import zz.top.p2p.camera.P2PSession;

public class ResolutionSend
{
    public final static byte RESOLUTION_AUTO = 0;
    public final static byte RESOLUTION_1080P = 1;
    public final static byte RESOLUTION_720P = 2;
    public final static byte RESOLUTION_SUPER1080P = 3;

    public int resolution;

    private P2PSession session;

    public ResolutionSend(P2PSession session, int resolution)
    {
        this.session = session;
        this.resolution = resolution;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.IPCAM_SET_RESOLUTION, build());

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
