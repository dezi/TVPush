package de.xavaro.android.tvpush;

import android.util.Base64;
import android.util.Log;

import de.xavaro.android.p2pcamera.P2PBarcode;
import de.xavaro.android.p2pcamera.P2PCamera;
import de.xavaro.android.p2pcamera.p2pcommands.PTZDirectionSend;
import de.xavaro.android.p2pcamera.p2pcommands.ResolutionSend;

public class CameraTest
{
    private static final String LOGTAG = CameraTest.class.getSimpleName();

    private static P2PCamera p2pcamera;

    public static String DID = "TNPUSAC-663761-TLWPW";
    public static String DPW = "IHQPekEX41IaZ4T";

    public static void initialize()
    {
        Log.d(LOGTAG, "#####" + Base64.encodeToString("1234abcd".getBytes(), 2));
        Log.d(LOGTAG, "#####" + new String(Base64.decode("RGV6aSBIb21l", 0)));
        Log.d(LOGTAG, "#####" + P2PBarcode.EncodeBarcodeString(false, "Dezi Home", "1234abcd", null));

        p2pcamera = new P2PCamera(DID, DPW);

        p2pcamera.connectCamera();

        p2pcamera.deviceInfoquery();

        p2pcamera.resolutionSend(ResolutionSend.RESOLUTION_1080P);

        p2pcamera.ptzDirectionSend(PTZDirectionSend.DIRECTION_LEFT, 0);

        p2pcamera.resolutionQuery();

        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p2pcamera.ptzHomeSend();
            }
        }, 5000);

        ApplicationBase.handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                p2pcamera.disconnectCamera();
                p2pcamera = null;
            }
        }, 15000);
    }

}
