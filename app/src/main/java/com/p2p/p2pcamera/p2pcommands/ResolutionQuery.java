package com.p2p.p2pcamera.p2pcommands;

import com.p2p.p2pcamera.P2PCommandCodes;
import com.p2p.p2pcamera.P2PMessage;
import com.p2p.p2pcamera.P2PSession;

public class ResolutionQuery
{
    private P2PSession session;

    public ResolutionQuery(P2PSession session)
    {
        this.session = session;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(P2PCommandCodes.IPCAM_GET_RESOLUTION, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        return new byte[4];
    }
}
