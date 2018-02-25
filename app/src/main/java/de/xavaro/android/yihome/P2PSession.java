package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;
import com.p2p.pppp_api.PPPP_Keys;
import com.p2p.pppp_api.PPPP_Session;

import de.xavaro.android.yihome.p2pcommands.DeviceInfoData;
import de.xavaro.android.yihome.p2pcommands.DeviceInfoQuery;
import de.xavaro.android.yihome.p2pcommands.ResolutionQuery;
import de.xavaro.android.yihome.p2pcommands.PTZControlStopSend;
import de.xavaro.android.yihome.p2pcommands.PTZDirectionSend;
import de.xavaro.android.yihome.p2pcommands.PTZHomeSend;
import de.xavaro.android.yihome.p2pcommands.PTZJumpSend;
import de.xavaro.android.yihome.p2pcommands.ResolutionData;
import de.xavaro.android.yihome.p2pcommands.ResolutionSend;

import static com.p2p.pppp_api.PPPP_APIs.PPPP_Check;

@SuppressWarnings({ "WeakerAccess"})
public class P2PSession
{
    private static final String LOGTAG = P2PSession.class.getSimpleName();

    public int session;
    public String targetId;
    public boolean isBigEndian;
    public boolean isConnected;

    private String account = "admin";
    private String password = "92DHWPDNdDDnYtz";

    private short cmdSequence;
    private long lastLoginTime;

    private PPPP_Session sessionInfo;

    static
    {
        System.loadLibrary("PPPP_API");

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
        if (! isConnected)
        {
            byte type = (byte) 5;
            byte magic = (byte) (((type << 1) | 1) | 64);

            Log.d(LOGTAG, "connect: magic=" + magic);

            session = PPPP_APIs.PPPP_ConnectByServer(targetId, magic, 0, PPPP_Keys.serverString, PPPP_Keys.licenseKey);
            Log.d(LOGTAG, "connect: PPPP_ConnectByServer=" + session);

            if (session <= 0)
            {
                //
                // Device not found or session cannot be created.
                //

                return false;
            }

            //
            // Todo: Should be retrieved somehow from session / connect data.
            //

            isBigEndian = true;
            isConnected = true;

            //
            // Retrieve basic session info.
            //

            sessionInfo = new PPPP_Session();

            int resCheck = PPPP_Check(session, sessionInfo);

            Log.d(LOGTAG, "initialize: PPPP_Check=" + resCheck);

            if (resCheck == 0)
            {
                Log.d(LOGTAG, "connect: getRemoteIP=" + sessionInfo.getRemoteIP());
                Log.d(LOGTAG, "connect: getRemotePort=" + sessionInfo.getRemotePort());
            }

            P2PReaderThread t0 = new P2PReaderThreadCommand(this);
            P2PReaderThread t1 = new P2PReaderThread(this, (byte) 1);
            P2PReaderThread t2 = new P2PReaderThread(this, (byte) 2);
            P2PReaderThread t3 = new P2PReaderThread(this, (byte) 3);
            P2PReaderThread t4 = new P2PReaderThread(this, (byte) 4);
            P2PReaderThread t5 = new P2PReaderThread(this, (byte) 5);

            t0.start();
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();
        }

        return isConnected;
    }

    public boolean close()
    {
        if (isConnected)
        {
            isConnected = false;

            int resClose = PPPP_APIs.PPPP_Close(session);
            Log.d(LOGTAG, "close: PPPP_Close=" + resClose);
        }

        return ! isConnected;
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

    //region Delegate section.

    public boolean resolutionSend(int resolution)
    {
        return (new ResolutionSend(this, resolution)).send();
    }

    public boolean resolutionQuery()
    {
        return (new ResolutionQuery(this)).send();
    }

    public boolean ptzDirectionSend(int direction, int speed)
    {
        return (new PTZDirectionSend(this, direction, speed)).send();
    }

    public boolean ptzJumpSend(int transverseProportion, int longitudinalProportion)
    {
        return (new PTZJumpSend(this, transverseProportion, longitudinalProportion)).send();
    }

    public boolean ptzControlStopSend()
    {
        return (new PTZControlStopSend(this)).send();
    }

    public boolean ptzHomeSend()
    {
        return (new PTZHomeSend(this)).send();
    }

    public boolean deviceInfoquery()
    {
        return (new DeviceInfoQuery(this)).send();
    }

    //endregion Delegate section.

    //region Listener section.

    //region OnDeviceInfoReceivedListener

    private OnDeviceInfoReceivedListener onDeviceInfoReceivedListener;

    public void setOnDeviceInfoReceivedListener(OnDeviceInfoReceivedListener listener)
    {
        onDeviceInfoReceivedListener = listener;
    }

    public OnDeviceInfoReceivedListener getOnDeviceInfoReceivedListener()
    {
        return onDeviceInfoReceivedListener;
    }

    public void onDeviceInfoReceived(DeviceInfoData deviceInfo)
    {
        Log.d(LOGTAG, "onDeviceInfoReceived:"
                + " version=" + deviceInfo.version
                + " total=" + deviceInfo.total
                + " free=" + deviceInfo.free
        );

        if (onDeviceInfoReceivedListener != null)
        {
            onDeviceInfoReceivedListener.onDeviceInfoReceived(deviceInfo);
        }
    }

    public interface OnDeviceInfoReceivedListener
    {
        void onDeviceInfoReceived(DeviceInfoData deviceInfo);
    }

    //endregion OnDeviceInfoReceivedListener

    //region OnResolutionReceivedListener

    private OnResolutionReceivedListener onResolutionReceivedListener;

    public void setOnResolutionReceivedListener(OnResolutionReceivedListener listener)
    {
        onResolutionReceivedListener = listener;
    }

    public OnResolutionReceivedListener getOnResolutionReceivedListener()
    {
        return onResolutionReceivedListener;
    }

    public void onResolutionReceived(ResolutionData resolution)
    {
        Log.d(LOGTAG, "onResolutionReceived:"
                + " resolution=" + resolution.resolution
                + " reserved=" + resolution.reserved
        );

        if (onResolutionReceivedListener != null)
        {
            onResolutionReceivedListener.onResolutionReceived(resolution);
        }
    }

    public interface OnResolutionReceivedListener
    {
        void onResolutionReceived(ResolutionData resolution);
    }

    //endregion OnResolutionReceivedListener

    //endregion Listener section.
}
