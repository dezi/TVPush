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

    public static String licenseKey = "MYXXFG";
    public static String serverString = "MNFFJLLHIFELKLHOOCEIPOEBGODBFPHGGKEALBIBIJBPCDBHPAIHABNCPPCFBFKDODKLDMPCNODIANDDBN";

    public static String DID = "TNPUSAC-663761-TLWPW";
    public static String cameraPassWord = "A8B5C7563090C89EE3D504CC5D68487E";

    public static String account = "admin";
    public static String password = "92DHWPDNdDDnYtz";

    //010300000000003013110001000000083369353462756E354B7952637432752C596D4A78746A726847394753473131000000000100000001
    //04010000000000301311000100000008455A777457616467324257715A37562C552F445152725076434B764255556D000000000100000001
    //0101000000000030131100010000000835573534546D4179377835763079622C4B6377624979634F4B4E532F4E5457000000000100000001

    public static void initialize()
    {
        p2psession = new P2PSession(DID);

        Log.d(LOGTAG, "initialize: device=" + DID);
        Log.d(LOGTAG, "initialize: isOnline=" + p2psession.isOnline());
        Log.d(LOGTAG, "initialize: connect=" + p2psession.connect());

        P2PReaderThread t0 = new P2PReaderThreadCommand(p2psession.session, isByteOrderBig);
        P2PReaderThread t1 = new P2PReaderThread(p2psession.session, 1, isByteOrderBig);
        P2PReaderThread t2 = new P2PReaderThread(p2psession.session, 2, isByteOrderBig);
        P2PReaderThread t3 = new P2PReaderThread(p2psession.session, 3, isByteOrderBig);
        P2PReaderThread t4 = new P2PReaderThread(p2psession.session, 4, isByteOrderBig);
        P2PReaderThread t5 = new P2PReaderThread(p2psession.session, 5, isByteOrderBig);

        t0.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        //setResolution(resConnect, 1);

        getDeviceInfo(p2psession.session);

        p2psession.sendPTZDirection(3, 0);

        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p2psession.sendPTZHome();
            }
        }, 5000);

        /*
        int resClose = PPPP_Close(resConnect);
        Log.d(LOGTAG, "initialize: PPPP_Close=" + resClose);

        int resDeinit = PPPP_DeInitialize();
        Log.d(LOGTAG, "initialize: PPPP_DeInitialize=" + resDeinit);
        */
    }

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

    public static void getDeviceInfo(int session)
    {
        Log.d(LOGTAG, "getDeviceInfo:");

        P2PMessage p2PMessage = new P2PMessage(
                (short) AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());

        Log.d(LOGTAG, "getDeviceInfo: command=" + Integer.toHexString(p2PMessage.reqId));
        Log.d(LOGTAG, "getDeviceInfo: p2pmess=" + Simple.getHexBytesToString(p2PMessage.data));

        packDatAndSend(session, p2PMessage);
    }

    private static void packDatAndSend(int session, P2PMessage p2PMessage)
    {
        short s = (short) p2PMessage.reqId;
        short s2 = (short) 1;

        String access$1500 = account;
        String access$1600 = password;

        Log.d(LOGTAG, "dezihack: " + access$1500);
        Log.d(LOGTAG, "dezihack: " + access$1600);

        if (true)
        {
            access$1500 = P2PUtil.genNonce(15);
            access$1600 = P2PUtil.getPassword(access$1500, access$1600);
        }

        Log.d(LOGTAG, "dezihack: " + access$1500);
        Log.d(LOGTAG, "dezihack: " + access$1600);

        // admin
        // W6OCfaN4O6Q0BcS
        // G566PXBU0ZnOZGH
        // 7D+MAnHQbeqQQoo
        // 010300000000002C03300003000000044735363650584255305A6E4F5A47482C37442B4D416E485162657151516F6F0000000000
        // 010300000000002C03300001000000044735363650584255305A6E4F5A47482C37442B4D416E485162657151516F6F0000000000

        P2PFrame tNPIOCtrlHead = new P2PFrame(s, s2, (short) p2PMessage.data.length, access$1500, access$1600, -1, isByteOrderBig);
        P2PHeader tNPHead = new P2PHeader((byte) 1, (byte) 3, (tNPIOCtrlHead.exHeaderSize + 40) + p2PMessage.data.length, isByteOrderBig);

        int i = tNPHead.dataSize + 8;

        byte[] obj = new byte[i];
        System.arraycopy(tNPHead.build(), 0, obj, 0, 8);
        System.arraycopy(tNPIOCtrlHead.build(), 0, obj, 8, 40);
        System.arraycopy(p2PMessage.data, 0, obj, tNPIOCtrlHead.exHeaderSize + 48, p2PMessage.data.length);

        Log.d(LOGTAG, "packDatAndSend: size=" + obj.length + " hex=" + Simple.getHexBytesToString(obj));

        int PPPP_Write = PPPP_APIs.PPPP_Write(session, (byte) 0, obj, i);

        Log.d(LOGTAG, "PPPP_Write IOCTRL, ret:" + PPPP_Write + ", cmdNum:" + tNPIOCtrlHead.commandNumber + ", extSize:" + tNPIOCtrlHead.exHeaderSize + ", send(" + session + ", 0x" + Integer.toHexString(p2PMessage.reqId) + ", " + Simple.getHexBytesToString(p2PMessage.data) + ")");
    }
}
