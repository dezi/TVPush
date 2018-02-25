package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;
import com.p2p.pppp_api.PPPP_Keys;

import static com.p2p.pppp_api.PPPP_APIs.PPPP_GetAPIVersion;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_Initialize;

@SuppressWarnings({ "WeakerAccess"})
public class P2PSession
{
    private static final String LOGTAG = P2PSession.class.getSimpleName();

    public String targetId;
    public int session;
    public boolean isBigEndian;

    private String account = "admin";
    private String password = "92DHWPDNdDDnYtz";

    private int cmdSequence;
    private long lastLoginTime;

    static
    {
        int resVersion = PPPP_GetAPIVersion();
        Log.d(LOGTAG, "static: PPPP_GetAPIVersion=" + resVersion);

        int resInit = PPPP_Initialize("".getBytes(), 12);
        Log.d(LOGTAG, "static: PPPP_Initialize=" + resInit);
    }

    public P2PSession(String targetId)
    {
        this.targetId = targetId;
    }

    public boolean isOnline()
    {
        int[] lastLoginTime = new int[2];

        int resCheckOnline = PPPP_APIs.PPPP_CheckDevOnline(targetId, PPPP_Keys.serverString, 2, lastLoginTime);

        Log.d(LOGTAG, "isOnline: PPPP_CheckDevOnline=" + resCheckOnline + " lastLoginTime=" + lastLoginTime[ 0 ]);

        if (resCheckOnline == 1) this.lastLoginTime = lastLoginTime[ 0 ];

        return (resCheckOnline == 1);
    }

    public boolean connect()
    {
        byte type = (byte) 5;
        byte magic = (byte) (((type << 1) | 1) | 64);

        Log.d(LOGTAG, "connect: magic=" + magic);

        session = PPPP_APIs.PPPP_ConnectByServer(targetId, magic, 0, PPPP_Keys.serverString, PPPP_Keys.licenseKey);
        Log.d(LOGTAG, "connect: PPPP_ConnectByServer=" + session);

        //
        // Todo: Should be retrieved somehow from session / connect data.
        //

        isBigEndian = true;

        return (session > 0);
    }

    public void packDatAndSend(P2PMessage p2PMessage)
    {
        short s = (short) p2PMessage.reqId;
        short s2 = (short) ++cmdSequence;

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

        TNPIOCtrlHead tNPIOCtrlHead = new TNPIOCtrlHead(s, s2, (short) p2PMessage.data.length, access$1500, access$1600, -1, isBigEndian);
        TNPHead tNPHead = new TNPHead((byte) 1, (byte) 3, (tNPIOCtrlHead.exHeaderSize + 40) + p2PMessage.data.length, isBigEndian);

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
