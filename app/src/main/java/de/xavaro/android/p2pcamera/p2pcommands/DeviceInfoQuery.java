package de.xavaro.android.p2pcamera.p2pcommands;

import de.xavaro.android.p2pcamera.P2PCommandCodes;
import de.xavaro.android.p2pcamera.P2PMessage;
import de.xavaro.android.p2pcamera.P2PSession;

public class DeviceInfoQuery
{
    private P2PSession session;

    public DeviceInfoQuery(P2PSession session)
    {
        this.session = session;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(P2PCommandCodes.IPCAM_DEVINFO_REQ, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        return new byte[4];
    }
}
