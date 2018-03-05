package zz.top.p2p.commands;

import zz.top.p2p.camera.P2PMessage;
import zz.top.p2p.camera.P2PPacker;
import zz.top.p2p.camera.P2PSession;

public class DayNightSend
{
    public final static byte DAYNIGHT_AUTO = 1;
    public final static byte DAYNIGHT_OFF = 2;
    public final static byte DAYNIGHT_ON = 3;

    public int daynight;
    public long fromTimeGMTMS;
    public long totoTimeGMTMS;

    private P2PSession session;

    public DayNightSend(P2PSession session, int daynight, long fromTimeGMTMS, long totoTimeGMTMS)
    {
        this.session = session;
        this.daynight = daynight;
        this.fromTimeGMTMS = fromTimeGMTMS;
        this.totoTimeGMTMS = totoTimeGMTMS;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.IPCAM_SET_DAYNIGHT_MODE, build());

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        byte[] fake = new byte[]{ 0x07,  (byte) 0xb2,  0x01, 0x01, 0x05, 0x00, 0x00, 0x00};

        byte[] data = new byte[32];

        System.arraycopy(P2PPacker.intToByteArray(daynight, session.isBigEndian), 0, data, 0, 4);
        System.arraycopy((new UtilTimeDay(fromTimeGMTMS, true, session.isBigEndian)).build(), 0, data, 4, 8);
        System.arraycopy((new UtilTimeDay(totoTimeGMTMS, true, session.isBigEndian)).build(), 0, data, 12, 8);

        return data;
    }

}
