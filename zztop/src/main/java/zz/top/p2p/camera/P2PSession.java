package zz.top.p2p.camera;

import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;

import java.security.Key;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import zz.top.gls.GLSFrame;
import zz.top.p2p.commands.DeviceInfoData;
import zz.top.p2p.commands.ResolutionData;

import zz.top.p2p.api.P2PApiKeys;
import zz.top.p2p.api.P2PApiNative;
import zz.top.p2p.api.P2PApiSession;
import zz.top.gls.GLSVideoView;

@SuppressWarnings({ "WeakerAccess"})
public class P2PSession
{
    private static final String LOGTAG = P2PSession.class.getSimpleName();

    public int session;

    public String targetId;
    public String targetPw;

    public boolean isBigEndian;
    public boolean isConnected;
    public boolean isFreetouse;
    public boolean isCorrupted;
    public boolean isEncrypted;

    private short cmdSequence;
    private long lastLoginTime;

    private P2PApiSession sessionInfo;

    private P2PReaderThread t0;
    private P2PReaderThread t1;
    private P2PReaderThread t2;
    private P2PReaderThread t3;
    private P2PReaderThread t4;
    private P2PReaderThread t5;

    public P2PAVFrameDecrypt p2pAVFrameDecrypt;

    private GLSVideoView videoView;

    static
    {
        System.loadLibrary("h265decoder");
        System.loadLibrary("zztopp2z");
        System.loadLibrary("zztopaac");
        System.loadLibrary("native-lib");

        int resVersion = P2PApiNative.GetAPIVersion();
        Log.d(LOGTAG, "static: P2PAPI.GetAPIVersion=" + resVersion);

        int resInit = P2PApiNative.Initialize("".getBytes(), 12);
        Log.d(LOGTAG, "static: P2PAPI.Initialize=" + resInit);

        int resShare = P2PApiNative.ShareBandwidth(0);
        Log.d(LOGTAG, "static: P2PAPI.ShareBandwidth=" + resShare);
    }

    public P2PSession()
    {
        //
        // Nothing done here...
        //
    }

    public boolean isOnline(String testId)
    {
        int[] lastLoginTime = new int[2];

        int resCheckOnline = P2PApiNative.CheckDevOnline(testId, P2PApiKeys.serverString, 2, lastLoginTime);

        Log.d(LOGTAG, "isOnline: P2PAPI.CheckDevOnline=" + resCheckOnline + " lastLoginTime=" + lastLoginTime[ 0 ]);

        if (resCheckOnline == 1) this.lastLoginTime = lastLoginTime[ 0 ];

        return (resCheckOnline == 1);
    }

    public boolean attachCamera(String targetId, String targetPw)
    {
        this.targetId = targetId;
        this.targetPw = targetPw;

        p2pAVFrameDecrypt = new P2PAVFrameDecrypt(targetPw + "0");

        return true;
    }

    public boolean connect()
    {
        if (! isConnected)
        {
            //
            // Should be 75.
            //

            byte type = (byte) 5;
            byte magic = (byte) (((type << 1) | 1) | 64);

            Log.d(LOGTAG, "connect: magic=75==" + magic);

            session = P2PApiNative.ConnectByServer(targetId, magic, 0, P2PApiKeys.serverString, P2PApiKeys.licenseKey);
            Log.d(LOGTAG, "connect: P2PAPI.ConnectByServer=" + session);

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
            isEncrypted = true;

            isConnected = true;
            isCorrupted = false;

            //
            // Retrieve basic session info.
            //


            sessionInfo = new P2PApiSession();

            /*
            int resCheck = P2PApiNative.Check(session, sessionInfo);
            Log.d(LOGTAG, "initialize: P2PAPI.Check=" + resCheck);

            if (resCheck == 0)
            {
                Log.d(LOGTAG, "connect: getRemoteIP=" + sessionInfo.getRemoteIP());
                Log.d(LOGTAG, "connect: getRemotePort=" + sessionInfo.getRemotePort());
            }
            */

            t0 = new P2PReaderThreadContl(this);
            t1 = new P2PReaderThreadAudio(this, (byte) 1);
            t2 = new P2PReaderThreadVideo(this, (byte) 2);
            t3 = new P2PReaderThreadVideo(this, (byte) 3);
            t4 = new P2PReaderThreadVideo(this, (byte) 4);
            t5 = new P2PReaderThreadVideo(this, (byte) 5);

            t0.start();
            t1.start();
            t2.start();
            t3.start();
            t4.start();
            t5.start();

            onConnectStateChanged(isConnected);
        }

        return isConnected;
    }

    public boolean disconnect()
    {
        synchronized (this)
        {
            if (isConnected)
            {
                t0.interrupt();
                t1.interrupt();
                t2.interrupt();
                t3.interrupt();
                t4.interrupt();
                t5.interrupt();

                int resClose = P2PApiNative.Close(session);
                Log.d(LOGTAG, "disconnect: P2PAPI.Close=" + resClose);

                if (resClose == 0)
                {
                    isConnected = false;

                    onConnectStateChanged(false);
                }
            }
        }

        return ! isConnected;
    }

    public void forceDisconnect()
    {
        synchronized (this)
        {
            if (isConnected)
            {
                t0.interrupt();
                t1.interrupt();
                t2.interrupt();
                t3.interrupt();
                t4.interrupt();
                t5.interrupt();

                int resClose = P2PApiNative.ForceClose(session);
                Log.d(LOGTAG, "forceDisconnect: P2PAPI.ForceClose=" + resClose);

                if (resClose == 0)
                {
                    isConnected = false;
                    onConnectStateChanged(false);
                }
            }
        }
    }

    public boolean setVideoView(FrameLayout videoView)
    {
        if (videoView instanceof GLSVideoView)
        {
            this.videoView = (GLSVideoView) videoView;

            return true;
        }

        return false;
    }

    public FrameLayout getVideoView()
    {
        return videoView;
    }

    public GLSVideoView getGLSVideoView()
    {
        return videoView;
    }

    public void renderFrame(GLSFrame avFrame)
    {
        videoView.renderFrame(avFrame);
    }

    public boolean packDatAndSend(P2PMessage p2PMessage)
    {
        String auth1 = "admin";
        String auth2 = targetPw;

        Log.d(LOGTAG, "packDatAndSend: dezihack: " + auth2);

        if (! isFreetouse)
        {
            auth1 = P2PUtil.genNonce(15);
            auth2 = getPassword(auth2, auth1);
        }

        Log.d(LOGTAG, "packDatAndSend: dezihack: " + auth1);
        Log.d(LOGTAG, "packDatAndSend: dezihack: " + auth2);

        P2PFrame p2pFrame = new P2PFrame(p2PMessage.reqId, ++cmdSequence, (short) p2PMessage.data.length, auth1, auth2, isBigEndian);
        P2PHeader p2pHead = new P2PHeader(P2PHeader.VERSION_ONE, (byte) 3, (p2pFrame.exHeaderSize + 40) + p2PMessage.data.length, isBigEndian);

        byte[] data = new byte[p2pHead.dataSize + 8];

        System.arraycopy(p2pHead.build(), 0, data, 0, 8);
        System.arraycopy(p2pFrame.build(), 0, data, 8, 40);
        System.arraycopy(p2PMessage.data, 0, data, p2pFrame.exHeaderSize + 48, p2PMessage.data.length);

        //Log.d(LOGTAG, "packDatAndSend: size=" + obj.length + " hex=" + P2PUtil.getHexBytesToString(obj));

        int xfer = P2PApiNative.Write(session, (byte) 0, data, data.length);

        Log.d(LOGTAG, "P2PApiNative.Write IOCTRL" +
                ", ret:" + xfer
                + ", cmdNum:" + p2pFrame.commandNumber
                + ", extSize:" + p2pFrame.exHeaderSize
                + ", send(" + session
                + ", 0x" + Integer.toHexString(p2PMessage.reqId)
                + "=" + p2PMessage.reqId
                + ", " + P2PUtil.getHexBytesToString(p2PMessage.data, true)
                + ")"
        );

        return (xfer == data.length);
    }

    public String getPassword(String key, String data)
    {
        String hmacSha1 = P2PUtil.hmacSha1(key, "user=xiaoyiuser&nonce=" + data);
        return hmacSha1.length() > 15 ? hmacSha1.substring(0, 15) : hmacSha1;
    }

    //region Listener section.

    //region OnConnectStateChangedListener

    private OnConnectStateChangedListener onConnectStateChangedListener;

    public void OnConnectStateChangedListener(OnConnectStateChangedListener listener)
    {
        onConnectStateChangedListener = listener;
    }

    public OnConnectStateChangedListener getOnConnectStateChangedListener()
    {
        return onConnectStateChangedListener;
    }

    public void onConnectStateChanged(boolean isConnected)
    {
        Log.d(LOGTAG, "onConnectStateChanged:"
                + " camera=" + targetId
                + " connected=" + isConnected
        );

        if (onConnectStateChangedListener != null)
        {
            onConnectStateChangedListener.onConnectStateChanged(isConnected);
        }
    }

    public interface OnConnectStateChangedListener
    {
        void onConnectStateChanged(boolean isConnected);
    }

    //endregion OnConnectStateChangedListener

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
