package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;

import de.xavaro.android.tvpush.ApplicationBase;
import de.xavaro.android.yihome.p2pcommands.PTZDirectionSend;
import de.xavaro.android.yihome.p2pcommands.ResolutionSend;

public class Camera
{
    private static final String LOGTAG = Camera.class.getSimpleName();

    private static P2PCamera p2pcamera;

    public static String DID = "TNPUSAC-663761-TLWPW";
    public static String DPW = "IHQPekEX41IaZ4T";

    public static void initialize()
    {
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
