package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PMessage;
import zz.top.p2p.camera.P2PPacker;
import zz.top.p2p.camera.P2PSession;

public class PTZJumpSend
{
    public int transverseProportion;
    public int longitudinalProportion;

    private P2PSession session;

    public PTZJumpSend(P2PSession session, int transverseProportion, int longitudinalProportion)
    {
        this.session = session;
        this.transverseProportion = transverseProportion;
        this.longitudinalProportion = longitudinalProportion;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.PTZ_JUMP_TO_POINT, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        byte[] data = new byte[8];

        System.arraycopy(P2PPacker.intToByteArray(transverseProportion, session.isBigEndian), 0, data, 0, 4);
        System.arraycopy(P2PPacker.intToByteArray(longitudinalProportion, session.isBigEndian), 0, data, 4, 4);

        return data;
    }
}
