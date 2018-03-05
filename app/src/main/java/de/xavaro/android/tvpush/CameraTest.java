package de.xavaro.android.tvpush;

import android.util.Base64;
import android.util.Log;

import zz.top.cam.Camera;
import zz.top.p2p.camera.P2PBarcode;
import zz.top.p2p.camera.P2PCamera;
import zz.top.cam.Cameras;
import zz.top.p2p.video.VideoGLVideoView;

public class CameraTest
{
    private static final String LOGTAG = CameraTest.class.getSimpleName();

    private static Camera camera;
    private static VideoGLVideoView surface;

    public static void initialize(VideoGLVideoView surfaceparam)
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
        String uuid = Cameras.findCameraByName(name);

        if (uuid == null)
        {
            Log.d(LOGTAG, "delayedInit: not fund=" + name);

            return;
        }

        camera = new P2PCamera();

        camera.attachCamera(uuid);
        camera.connectCamera();

        camera.setResolution(Camera.RESOLUTION_1080P);

        camera.startRealtimeVideo();
    }
}
