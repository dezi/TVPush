package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PMessage;
import zz.top.p2p.camera.P2PPacker;
import zz.top.p2p.camera.P2PSession;

public class LEDOnOffSend
{
    public int onoff;

    private P2PSession session;

    public LEDOnOffSend(P2PSession session, int onoff)
    {
        this.session = session;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.IPCAM_CLOSE_LIGHT_REQ, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        byte[] data = new byte[4];

        System.arraycopy(P2PPacker.intToByteArray(onoff, session.isBigEndian), 0, data, 0, 4);

        return data;
    }
}
