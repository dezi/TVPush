package de.xavaro.android.yihome.p2pcommands;

import de.xavaro.android.yihome.P2PCommandCodes;
import de.xavaro.android.yihome.P2PMessage;
import de.xavaro.android.yihome.P2PSession;

public class QueryDeviceInfo
{
    private P2PSession session;

    public QueryDeviceInfo(P2PSession session)
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
