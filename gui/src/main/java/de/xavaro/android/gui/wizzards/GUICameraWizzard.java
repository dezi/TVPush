package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.plugin.GUIPluginTitleIOT;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.things.IOTThing;
import de.xavaro.android.pub.interfaces.pub.PUBCamera;
import de.xavaro.android.pub.interfaces.pub.PUBSurface;

@SuppressWarnings("CanBeFinal")
public class GUICameraWizzard extends GUIPluginTitleIOT
{
    private final static String LOGTAG = GUICameraWizzard.class.getSimpleName();

    private GUIFrameLayout mainFrame;
    private FrameLayout videoSurface;
    private PUBCamera camera;

    private int zoom;

    public GUICameraWizzard(Context context)
    {
        super(context);

        setWizzard(true, true, 2, Gravity.END);

        GUIFrameLayout padFrame = new GUIFrameLayout(context);
        padFrame.setPaddingDip(GUIDefs.PADDING_SMALL);

        contentFrame.addView(padFrame);

        mainFrame = new GUIFrameLayout(context)
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                return onKeyDownDoit(keyCode, event) || super.onKeyDown(keyCode, event);
            }
        };

        String toastFocus = "Drücken Sie " + GUIDefs.UTF_OK + " um die Kamera zu steuern";

        mainFrame.setSizeDip(Simple.MP, Simple.MP);
        mainFrame.setHighlightable(true);
        mainFrame.setFocusable(true);
        mainFrame.setToastFocus(toastFocus);

        padFrame.addView(mainFrame);

        mainFrame.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Log.d(LOGTAG, "onClick: mainFrame.");

                mainFrame.setHighlight(! mainFrame.getHighlight());
            }
        });
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        connectCamera();
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        releaseCamera();
    }

    private void connectCamera()
    {
        if (camera == null)
        {
            IOTDevice device = new IOTDevice(uuid);
            IOTStatus status = new IOTStatus(uuid);
            IOTCredential credential = new IOTCredential(uuid);

            camera = GUI.instance.onCameraHandlerRequest(
                    device.toJson(),
                    status.toJson(),
                    credential.toJson());

            if (camera == null) return;

            if (videoSurface == null)
            {
                videoSurface = camera.createSurface(getContext());
                mainFrame.addView(videoSurface);
                camera.registerSurface(videoSurface);
            }

            camera.connectCamera();
            camera.setResolution(PUBCamera.RESOLUTION_720P);
            camera.startRealtimeVideo();
        }
    }

    private void releaseCamera()
    {
        if (camera != null)
        {
            if (videoSurface != null)
            {
                if (videoSurface.getParent() != null)
                {
                    mainFrame.removeView(videoSurface);
                }

                videoSurface = null;
            }

            camera.disconnectCamera();
            camera = null;
        }
    }

    private boolean onKeyDownDoit(int keyCode, KeyEvent event)
    {
        Log.d(LOGTAG, "onKeyDown: event=" + event);

        boolean usedKey = false;

        if (mainFrame.getHighlight())
        {
            usedKey = moveCam(keyCode);
        }

        return usedKey;
    }

    @Override
    public void setIOTObject(String uuid)
    {
        super.setIOTObject(uuid);

        IOTThing iotThing = IOTThing.getEntry(uuid);

        if (iotThing instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotThing;

            releaseCamera();
            connectCamera();

            String toastHighlight = ""
                    + "Zoomen mit" + " " + GUIDefs.UTF_ZOOMIN
                    + " und" + " " + GUIDefs.UTF_ZOOMOUT;

            if (device.hasCapability("pan") && device.hasCapability("tilt"))
            {
                toastHighlight = ""
                        + "Bewegen mit" + " " + GUIDefs.UTF_MOVE
                        + ", zoomen mit" + " " + GUIDefs.UTF_ZOOMIN
                        + " und" + " " + GUIDefs.UTF_ZOOMOUT;
            }

            mainFrame.setToastHighlight(toastHighlight);
        }
    }

    private boolean moveCam(int keyCode)
    {
        boolean usedkey = false;

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            startMove(PUBCamera.PTZ_DIRECTION_LEFT);
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            startMove(PUBCamera.PTZ_DIRECTION_UP);
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            startMove(PUBCamera.PTZ_DIRECTION_RIGHT);
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            startMove(PUBCamera.PTZ_DIRECTION_DOWN);
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND)
        {
            if (zoom > 0) zoom -= 1;
            setZoom();
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        {
            if (zoom < 8) zoom += 1;
            setZoom();
            usedkey = true;
        }

        return usedkey;
    }

    private void setZoom()
    {
        if ((videoSurface != null) && (videoSurface instanceof PUBSurface))
        {
            ((PUBSurface) videoSurface).setZoom(zoom, 50);
        }
    }

    private void startMove(int dir)
    {
        camera.startPTZDirection(dir, 1);

        Simple.getHandler().removeCallbacks(stopMove);
        Simple.getHandler().postDelayed(stopMove, 250);
    }

    private final Runnable stopMove = new Runnable()
    {
        @Override
        public void run()
        {
            camera.stopPTZDirection();
        }
    };
}