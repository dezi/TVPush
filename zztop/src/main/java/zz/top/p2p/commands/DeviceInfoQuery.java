package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PMessage;
import zz.top.p2p.camera.P2PSession;

public class DeviceInfoQuery
{
    private P2PSession session;

    public DeviceInfoQuery(P2PSession session)
    {
        this.session = session;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.IPCAM_DEVINFO_REQ, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        return new byte[4];
    }
}
