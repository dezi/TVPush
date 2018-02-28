package com.p2p.p2pcamera.p2pcommands;

import com.p2p.p2pcamera.P2PMessage;
import com.p2p.p2pcamera.P2PPacker;
import com.p2p.p2pcamera.P2PSession;

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
