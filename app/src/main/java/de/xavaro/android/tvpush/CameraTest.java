package de.xavaro.android.tvpush;

import android.util.Base64;
import android.util.Log;

import com.p2p.p2pcamera.P2PBarcode;
import com.p2p.p2pcamera.P2PCamera;
import com.p2p.p2pcamera.P2PCameras;
import com.p2p.p2pcamera.P2PVideoGLSurfaceView;

public class CameraTest
{
    private static final String LOGTAG = CameraTest.class.getSimpleName();

    private static P2PCamera p2pcamera;
    private static P2PVideoGLSurfaceView surface;

    public static String DID = "TNPUSAC-663761-TLWPW";
    public static String DPW = "IHQPekEX41IaZ4T";

    public static void initialize(P2PVideoGLSurfaceView surfaceparam)
    {
        surface = surfaceparam;

        Log.d(LOGTAG, "#####" + Base64.encodeToString("1234abcd".getBytes(), 2));
        Log.d(LOGTAG, "#####" + new String(Base64.decode("RGV6aSBIb21l", 0)));
        Log.d(LOGTAG, "#####" + P2PBarcode.EncodeBarcodeString(false, "Dezi Home", "1234abcd", null));

        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                delayedInit("Dezi's Domcam #1");
            }
        }, 2000);
    }

    private static void delayedInit(String name)
    {
        String uuid = P2PCameras.findCameraByName(name);

        if (uuid == null)
        {
            Log.d(LOGTAG, "delayedInit: not fund=" + name);

            return;
        }

        String did = P2PCameras.getP2PDeviceId(uuid);
        String dpw = P2PCameras.getP2PDevicePw(uuid);

        Log.d(LOGTAG, "delayedInit: fund=" + name + " did=" + did + " dpw=" + dpw);

        if ((did == null) || (dpw == null)) return;

        p2pcamera = new P2PCamera(uuid, did, dpw, surface);

        p2pcamera.connectCamera();

        //p2pcamera.deviceInfoQuery();
        //p2pcamera.resolutionQuery();

        //p2pcamera.resolutionSend(P2PCamera.RESOLUTION_1080P);

        //p2pcamera.ptzDirectionSend(P2PCamera.PTZ_DIRECTION_LEFT, 0);

        p2pcamera.startVideoSend(P2PCamera.STARTREALTIME_RESOLUTION_PREVIEW);
        //p2pcamera.startAudioSend();

        /*
        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p2pcamera.ptzHomeSend();
            }
        }, 5000);
        */

        /*
        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p2pcamera.disconnectCamera();
                p2pcamera = null;
            }
        }, 15000);
        */
    }
}
