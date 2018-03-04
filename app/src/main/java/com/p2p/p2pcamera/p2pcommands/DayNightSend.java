package com.p2p.p2pcamera.p2pcommands;

import com.p2p.p2pcamera.P2PMessage;
import com.p2p.p2pcamera.P2PPacker;
import com.p2p.p2pcamera.P2PSession;

import java.util.Calendar;
import java.util.TimeZone;

public class DayNightSend
{
    public final static int DAYNIGHT_AUTO = 1;
    public final static int DAYNIGHT_OFF = 2;
    public final static int DAYNIGHT_ON = 3;

    public int daynight;
    public long fromtime;
    public long tototime;

    private P2PSession session;

    public DayNightSend(P2PSession session, int daynight, long fromtime, long tototime)
    {
        this.session = session;
        this.daynight = daynight;
        this.fromtime = fromtime;
        this.tototime = tototime;
    }

    public boolean send()
    {
        P2PMessage p2PMessage = new P2PMessage(CommandCodes.IPCAM_SET_DAYNIGHT_MODE, build());

        //send(1, 0x1321, 00 00 00 01 07 B2 01 01 05 00 00 00 07 B2 01 01 05 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 )

        return session.packDatAndSend(p2PMessage);
    }

    public byte[] build()
    {
        /*
        Calendar instance = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        Calendar instance2 = Calendar.getInstance(TimeZone.getTimeZone("gmt"));
        instance.setTimeInMillis(j);
        instance2.setTimeInMillis(j2);
        Object obj = new byte[32];
        System.arraycopy(Packet.intToByteArray(i, z), 0, obj, 0, 4);
        System.arraycopy(STimeDay.parseContent(instance.get(1), instance.get(2) + 1, instance.get(5), instance.get(7), instance.get(11), instance.get(12), 0, z), 0, obj, 4, 8);
        System.arraycopy(STimeDay.parseContent(instance2.get(1), instance2.get(2) + 1, instance2.get(5), instance2.get(7), instance2.get(11), instance2.get(12), 0, z), 0, obj, 12, 8);
        return obj;
        */

        byte[] fake = new byte[]{ 0x07,  (byte) 0xb2,  0x01, 0x01, 0x05, 0x00, 0x00, 0x00};

        byte[] data = new byte[32];

        System.arraycopy(P2PPacker.intToByteArray(daynight, session.isBigEndian), 0, data, 0, 4);
        System.arraycopy(fake, 0, data, 4, 8);
        System.arraycopy(fake, 0, data, 12, 8);

        /*
        System.arraycopy(STimeDay.parseContent(instance.get(1), instance.get(2) + 1, instance.get(5), instance.get(7), instance.get(11), instance.get(12), 0, z), 0, obj, 4, 8);
        System.arraycopy(STimeDay.parseContent(instance2.get(1), instance2.get(2) + 1, instance2.get(5), instance2.get(7), instance2.get(11), instance2.get(12), 0, z), 0, obj, 12, 8);
        */

        return data;
    }

}
