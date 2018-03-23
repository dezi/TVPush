package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.gui.base.GUIPluginTitleIOT;
import de.xavaro.android.gui.views.GUIFrameLayout;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.gui.simple.Simple;

import pub.android.interfaces.drv.Camera;

public class GUICameraWizzard extends GUIPluginTitleIOT
{
    private final static String LOGTAG = GUICameraWizzard.class.getSimpleName();

    private GUIFrameLayout mainFrame;
    private FrameLayout videoSurface;
    private Camera camera;

    private int zoom;

    public GUICameraWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, true, 2, Gravity.RIGHT);

        mainFrame = new GUIFrameLayout(context)
        {
            @Override
            public boolean onKeyDown(int keyCode, KeyEvent event)
            {
                return onKeyDownDoit(keyCode, event) || super.onKeyDown(keyCode, event);
            }

            @Override
            public void onHighlightChanged(View view, boolean highlight)
            {
                if (! highlight)
                {

                }
            }
        };

        final String toastFocus = ""
                + "Drücken Sie "
                + GUIDefs.UTF_OK
                + " um die Kamera zu bewegen";

        final String toastHighlight = ""
                + "Bewegen mit" + " " + GUIDefs.UTF_MOVE + " "
                + ", zoomen mit" + " " + GUIDefs.UTF_ZOOMIN
                + " und" + " " + GUIDefs.UTF_ZOOMOUT;

        mainFrame.setSizeDip(Simple.MP, Simple.MP);
        mainFrame.setPaddingDip(GUIDefs.PADDING_SMALL);
        mainFrame.setHighlightable(true);
        mainFrame.setFocusable(true);
        mainFrame.setToastFocus(toastFocus);
        mainFrame.setToastHighlight(toastHighlight);

        contentFrame.addView(mainFrame);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        IOTStatus status = new IOTStatus(iotObject.uuid);
        IOTCredential credential = new IOTCredential(iotObject.uuid);

        if (camera == null)
        {
            camera = GUI.instance.onCameraHandlerRequest(
                    iotObject.toJson(),
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
            camera.setResolution(Camera.RESOLUTION_720P);
            camera.startRealtimeVideo();
        }
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

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
            usedKey = moveMap(keyCode);
        }

        return usedKey;
    }

    @Override
    public void setIOTObject(IOTObject iotObject)
    {
        super.setIOTObject(iotObject);

        if (iotObject instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotObject;

        }
    }

    private boolean moveMap(int keyCode)
    {
        boolean usedkey = false;

        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
        {
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
        {
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
        {
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
        {
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_REWIND)
        {
            if (zoom > 15) zoom -= 1;
            usedkey = true;
        }

        if (keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD)
        {
            if (zoom < 20) zoom += 1;
            usedkey = true;
        }

        if (usedkey)
        {
        }

        return usedkey;
    }
}