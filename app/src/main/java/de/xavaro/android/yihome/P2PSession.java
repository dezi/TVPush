package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;
import com.p2p.pppp_api.PPPP_Keys;
import com.p2p.pppp_api.PPPP_Session;

import de.xavaro.android.yihome.p2pcommands.SendPTZDirection;
import de.xavaro.android.yihome.p2pcommands.SendPTZHome;

import static com.p2p.pppp_api.PPPP_APIs.PPPP_Check;

@SuppressWarnings({ "WeakerAccess"})
public class P2PSession
{
    private static final String LOGTAG = P2PSession.class.getSimpleName();

    public String targetId;
    public int session;
    public boolean isBigEndian;

    private String account = "admin";
    private String password = "92DHWPDNdDDnYtz";

    private short cmdSequence;
    private long lastLoginTime;

    private PPPP_Session sessionInfo;

    static
    {
        int resVersion = PPPP_APIs.PPPP_GetAPIVersion();
        Log.d(LOGTAG, "static: PPPP_GetAPIVersion=" + resVersion);

        int resInit = PPPP_APIs.PPPP_Initialize("".getBytes(), 12);
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

        if (session > 0)
        {
            sessionInfo = new PPPP_Session();

            int resCheck = PPPP_Check(session, sessionInfo);

            Log.d(LOGTAG, "initialize: PPPP_Check=" + resCheck);

            if (resCheck == 0)
            {
                Log.d(LOGTAG, "initialize: getRemoteIP=" + sessionInfo.getRemoteIP());
                Log.d(LOGTAG, "initialize: getRemotePort=" + sessionInfo.getRemotePort());

                return true;
            }
        }

        return false;
    }

    public boolean packDatAndSend(P2PMessage p2PMessage)
    {
        String auth1 = account;
        String auth2 = password;

        Log.d(LOGTAG, "packDatAndSend: dezihack: " + auth1);
        Log.d(LOGTAG, "packDatAndSend: dezihack: " + auth2);

        if (true)
        {
            auth1 = P2PUtil.genNonce(15);
            auth2 = P2PUtil.getPassword(auth1, auth2);
        }

        Log.d(LOGTAG, "packDatAndSend: dezihack: " + auth1);
        Log.d(LOGTAG, "packDatAndSend: dezihack: " + auth2);

        P2PFrame tNPIOCtrlHead = new P2PFrame(p2PMessage.reqId, ++cmdSequence, (short) p2PMessage.data.length, auth1, auth2, -1, isBigEndian);
        P2PHeader tNPHead = new P2PHeader((byte) 1, (byte) 3, (tNPIOCtrlHead.exHeaderSize + 40) + p2PMessage.data.length, isBigEndian);

        int i = tNPHead.dataSize + 8;

        byte[] obj = new byte[i];
        System.arraycopy(tNPHead.build(), 0, obj, 0, 8);
        System.arraycopy(tNPIOCtrlHead.build(), 0, obj, 8, 40);
        System.arraycopy(p2PMessage.data, 0, obj, tNPIOCtrlHead.exHeaderSize + 48, p2PMessage.data.length);

        Log.d(LOGTAG, "packDatAndSend: size=" + obj.length + " hex=" + Simple.getHexBytesToString(obj));

        int PPPP_Write = PPPP_APIs.PPPP_Write(session, (byte) 0, obj, i);

        Log.d(LOGTAG, "PPPP_Write IOCTRL, ret:" + PPPP_Write + ", cmdNum:" + tNPIOCtrlHead.commandNumber + ", extSize:" + tNPIOCtrlHead.exHeaderSize + ", send(" + session + ", 0x" + Integer.toHexString(p2PMessage.reqId) + ", " + Simple.getHexBytesToString(p2PMessage.data) + ")");

        return (PPPP_Write == i);
    }

    public boolean sendPTZDirection(int direction, int speed)
    {
        return (new SendPTZDirection(this, direction, speed)).send();
    }

    public boolean sendPTZHome()
    {
        return (new SendPTZHome(this)).send();
    }
}
