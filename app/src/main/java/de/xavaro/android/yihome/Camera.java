package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;

import de.xavaro.android.tvpush.ApplicationBase;

public class Camera
{
    private static final String LOGTAG = Camera.class.getSimpleName();

    private static boolean isByteOrderBig = true;

    private static P2PSession p2psession;

    static
    {
        System.loadLibrary("yihome-lib");
        System.loadLibrary("PPPP_API");
    }

    public static String DID = "TNPUSAC-663761-TLWPW";

    public static void initialize()
    {
        p2psession = new P2PSession(DID);

        Log.d(LOGTAG, "initialize: device=" + DID);
        Log.d(LOGTAG, "initialize: isOnline=" + p2psession.isOnline());
        Log.d(LOGTAG, "initialize: connect=" + p2psession.connect());

        p2psession.deviceInfoquery();

        p2psession.ptzDirectionSend(3, 0);

        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p2psession.ptzHomeSend();
            }
        }, 5000);

        /*
        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p2psession.close();
            }
        }, 15000);
        */
    }

    /*
    public static void setResolution(int session, int resolution)
    {
        byte[] obj = new byte[8];

        System.arraycopy(Packet.intToByteArray(resolution, isByteOrderBig), 0, obj, 0, 4);
        System.arraycopy(Packet.intToByteArray(1, isByteOrderBig), 0, obj, 4, 4);

        P2PMessage p2PMessage = new P2PMessage(
                (short) AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_RESOLUTION,
                obj);

        Log.d(LOGTAG, "setResolution: command=" + Integer.toHexString(p2PMessage.reqId));
        Log.d(LOGTAG, "setResolution: p2pmess=" + Simple.getHexBytesToString(p2PMessage.data));

        packDatAndSend(session, p2PMessage);
    }

    public static void sendPTZJump(int session, int transverseProportion, int longitudinalProportion)
    {
        Log.d(LOGTAG, "sendPTZJump:"
                + " transverseProportion=" + transverseProportion
                + " longitudinalProportion=" + longitudinalProportion);

        P2PMessage p2PMessage = new P2PMessage(
                (short) AVIOCTRLDEFs.IOTYPE_USER_PTZ_JUMP_TO_POINT,
                AVIOCTRLDEFs.SMsgAVIoctrlPTZJumpPointSet.parseContent(transverseProportion, longitudinalProportion, isByteOrderBig));

        packDatAndSend(session, p2PMessage);
    }
    */
}
