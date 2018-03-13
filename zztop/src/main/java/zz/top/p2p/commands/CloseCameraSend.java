package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PMessage;
import zz.top.p2p.camera.P2PPacker;
import zz.top.p2p.camera.P2PSession;

public class CloseCameraSend
{
    public int closed;

    private P2PSession session;

    public CloseCameraSend(P2PSession session, boolean closed)
    {
        this.session = session;

        this.closed = closed ? 1 : 0;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.IPCAM_CLOSE_CAMERA_REQ, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        byte[] data = new byte[4];

        System.arraycopy(P2PPacker.intToByteArray(closed, session.isBigEndian), 0, data, 0, 4);

        return data;
    }
}
