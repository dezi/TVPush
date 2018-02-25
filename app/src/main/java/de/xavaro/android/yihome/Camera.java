package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;
import com.p2p.pppp_api.PPPP_Keys;
import com.p2p.pppp_api.PPPP_Session;

import static com.p2p.pppp_api.PPPP_APIs.PPPP_Check;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_GetAPIVersion;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_Initialize;

public class Camera
{
    private static final String LOGTAG = Camera.class.getSimpleName();

    private static boolean isByteOrderBig = true;

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
    public static String password = "W6OCfaN4O6Q0BcS";

    public static int resConnect;
    public static int cmdNum;

    //010300000000003013110001000000083369353462756E354B7952637432752C596D4A78746A726847394753473131000000000100000001
    //04010000000000301311000100000008455A777457616467324257715A37562C552F445152725076434B764255556D000000000100000001
    //0101000000000030131100010000000835573534546D4179377835763079622C4B6377624979634F4B4E532F4E5457000000000100000001

    public static void initialize()
    {
        int resVersion = PPPP_GetAPIVersion();
        Log.d(LOGTAG, "initialize: PPPP_GetAPIVersion=" + resVersion);

        int resInit = PPPP_Initialize("".getBytes(), 12);
        Log.d(LOGTAG, "initialize: PPPP_Initialize=" + resInit);

        int[] iArr = new int[1];
        int resCheckOnline = PPPP_APIs.PPPP_CheckDevOnline(DID, serverString, 2, iArr);
        Log.d(LOGTAG, "initialize: PPPP_CheckDevOnline=" + resCheckOnline + " lastLoginTime=" + iArr[ 0 ]);

        //resConnect = PPPP_ConnectOnlyLanSearch("TNPUSAC-663761-TLWPW");
        //Log.d(LOGTAG, "initialize: PPPP_ConnectOnlyLanSearch=" + resConnect);

        byte i = (byte) 5;
        byte b = (byte) ((((i << 1) | 1) | 0) | 64);

        cmdNum = 0;
        resConnect = PPPP_APIs.PPPP_ConnectByServer(DID, (byte) 1, 0, serverString, licenseKey);
        Log.d(LOGTAG, "initialize: PPPP_ConnectByServer=" + resConnect);

        //resConnect = PPPP_APIs.PPPP_ConnectOnlyLanSearch(DID);
        //Log.d(LOGTAG, "initialize: PPPP_ConnectOnlyLanSearch=" + resConnect);

        PPPP_Session session = new PPPP_Session();

        int resCheck = PPPP_Check(resConnect, session);
        Log.d(LOGTAG, "initialize: PPPP_Check=" + resCheck);

        Log.d(LOGTAG, "initialize: getRemoteIP=" + session.getRemoteIP());
        Log.d(LOGTAG, "initialize: getRemotePort=" + session.getRemotePort());

        TNPReaderThread t0 = new TNPReaderCommandThread(resConnect, isByteOrderBig);
        TNPReaderThread t1 = new TNPReaderThread(resConnect, 1, isByteOrderBig);
        TNPReaderThread t2 = new TNPReaderThread(resConnect, 2, isByteOrderBig);
        TNPReaderThread t3 = new TNPReaderThread(resConnect, 3, isByteOrderBig);
        TNPReaderThread t4 = new TNPReaderThread(resConnect, 4, isByteOrderBig);
        TNPReaderThread t5 = new TNPReaderThread(resConnect, 5, isByteOrderBig);

        t0.start();
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        //setResolution(resConnect, 1);

        getDeviceInfo(resConnect);

        //sendPTZJump(resConnect, -10, 0);

        //sendPTZHome(resConnect);

        sendPanDirection(resConnect, 4, 0);

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
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_RESOLUTION,
                obj);

        Log.d(LOGTAG, "setResolution: command=" + Integer.toHexString(p2PMessage.reqId));
        Log.d(LOGTAG, "setResolution: p2pmess=" + Simple.getHexBytesToString(p2PMessage.data));

        packDatAndSend(session, p2PMessage);
    }

    public static void sendPTZHome(int session)
    {
        Log.d(LOGTAG, "sendPTZHome:");

        P2PMessage p2PMessage = new P2PMessage(
                AVIOCTRLDEFs.IOTYPE_USER_PTZ_HOME,
                new byte[4]);

        Log.d(LOGTAG, "sendPTZHome: command=" + Integer.toHexString(p2PMessage.reqId));
        Log.d(LOGTAG, "sendPTZHome: p2pmess=" + Simple.getHexBytesToString(p2PMessage.data));

        packDatAndSend(session, p2PMessage);
    }

    public static void sendPTZJump(int session, int transverseProportion, int longitudinalProportion)
    {
        Log.d(LOGTAG, "sendPTZJump:"
                + " transverseProportion=" + transverseProportion
                + " longitudinalProportion=" + longitudinalProportion);

        P2PMessage p2PMessage = new P2PMessage(
                AVIOCTRLDEFs.IOTYPE_USER_PTZ_JUMP_TO_POINT,
                AVIOCTRLDEFs.SMsgAVIoctrlPTZJumpPointSet.parseContent(transverseProportion, longitudinalProportion, isByteOrderBig));

        packDatAndSend(session, p2PMessage);
    }

    public static void sendPanDirection(int session, int direction, int speed)
    {
        Log.d(LOGTAG, "sendPanDirection: direction=" + direction + " speed=" + speed);

        P2PMessage p2PMessage = new P2PMessage(
                AVIOCTRLDEFs.IOTYPE_USER_PTZ_DIRECTION_CTRL,
                AVIOCTRLDEFs.SMsgAVIoctrlPTZDireCTRL.parseContent(direction, speed, isByteOrderBig));

        Log.d(LOGTAG, "sendPanDirection: command=" + Integer.toHexString(p2PMessage.reqId));
        Log.d(LOGTAG, "sendPanDirection: p2pmess=" + Simple.getHexBytesToString(p2PMessage.data));

        packDatAndSend(session, p2PMessage);
    }

    public static void getDeviceInfo(int session)
    {
        Log.d(LOGTAG, "getDeviceInfo:");

        P2PMessage p2PMessage = new P2PMessage(
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());

        Log.d(LOGTAG, "getDeviceInfo: command=" + Integer.toHexString(p2PMessage.reqId));
        Log.d(LOGTAG, "getDeviceInfo: p2pmess=" + Simple.getHexBytesToString(p2PMessage.data));

        packDatAndSend(session, p2PMessage);
    }

    private static void packDatAndSend(int session, P2PMessage p2PMessage)
    {
        short s = (short) p2PMessage.reqId;
        short s2 = (short) cmdNum;

        String access$1500 = account;
        String access$1600 = password;

        Log.d(LOGTAG, "dezihack: " + access$1500);
        Log.d(LOGTAG, "dezihack: " + access$1600);

        if (true)
        {
            access$1500 = Util.genNonce(15);
            access$1600 = Util.getPassword(access$1500, access$1600);
        }

        Log.d(LOGTAG, "dezihack: " + access$1500);
        Log.d(LOGTAG, "dezihack: " + access$1600);

        // admin
        // W6OCfaN4O6Q0BcS
        // G566PXBU0ZnOZGH
        // 7D+MAnHQbeqQQoo
        // 010300000000002C03300003000000044735363650584255305A6E4F5A47482C37442B4D416E485162657151516F6F0000000000
        // 010300000000002C03300001000000044735363650584255305A6E4F5A47482C37442B4D416E485162657151516F6F0000000000

        TNPIOCtrlHead tNPIOCtrlHead = new TNPIOCtrlHead(s, s2, (short) p2PMessage.data.length, access$1500, access$1600, -1, isByteOrderBig);
        TNPHead tNPHead = new TNPHead((byte) 1, (byte) 3, (tNPIOCtrlHead.exHeaderSize + 40) + p2PMessage.data.length, isByteOrderBig);

        int i = tNPHead.dataSize + 8;

        byte[] obj = new byte[i];
        System.arraycopy(tNPHead.toByteArray(), 0, obj, 0, 8);
        System.arraycopy(tNPIOCtrlHead.toByteArray(), 0, obj, 8, 40);
        System.arraycopy(p2PMessage.data, 0, obj, tNPIOCtrlHead.exHeaderSize + 48, p2PMessage.data.length);

        Log.d(LOGTAG, "packDatAndSend: size=" + obj.length + " hex=" + Simple.getHexBytesToString(obj));

        int PPPP_Write = PPPP_APIs.PPPP_Write(session, (byte) 0, obj, i);

        Log.d(LOGTAG, "PPPP_Write IOCTRL, ret:" + PPPP_Write + ", cmdNum:" + tNPIOCtrlHead.commandNumber + ", extSize:" + tNPIOCtrlHead.exHeaderSize + ", send(" + session + ", 0x" + Integer.toHexString(p2PMessage.reqId) + ", " + Simple.getHexBytesToString(p2PMessage.data) + ")");
    }
}
