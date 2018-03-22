package de.xavaro.android.gui.smart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIActivity;
import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.wizzards.GUICamerasWizzard;
import de.xavaro.android.gui.wizzards.GUIChannelWizzard;
import de.xavaro.android.gui.wizzards.GUIGeomapWizzard;
import de.xavaro.android.gui.plugin.GUIToastBar;
import de.xavaro.android.gui.wizzards.GUILocationsWizzard;
import de.xavaro.android.gui.wizzards.GUIPermissionWizzard;
import de.xavaro.android.gui.wizzards.GUIPingWizzard;
import de.xavaro.android.gui.wizzards.GUITodoWizzard;
import de.xavaro.android.gui.plugin.GUIVideoSurface;

import de.xavaro.android.iot.base.IOTObject;
import pub.android.interfaces.drv.Camera;

public class GUIDesktopActivity extends GUIActivity
{
    private final static String LOGTAG = GUIDesktopActivity.class.getSimpleName();

    public GUIToastBar speechRecognition;

    public GUIPingWizzard pingWizzard;
    public GUITodoWizzard todoWizzard;
    public GUICamerasWizzard cameraWizzard;
    public GUIChannelWizzard channelWizzard;
    public GUILocationsWizzard locationsWizzard;
    public GUIPermissionWizzard permissionsWizzard;

    public GUIGeomapWizzard geomapWizzard;

    private GUIVideoSurface videoSurface1;
    private GUIVideoSurface videoSurface2;
    private GUIVideoSurface videoSurface3;
    private GUIVideoSurface videoSurface4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GUI.instance.desktopActivity = this;

        speechRecognition = new GUIToastBar(this);
        topframe.addView(speechRecognition, speechRecognition.getPreferredLayout());

        pingWizzard = new GUIPingWizzard(this);
        todoWizzard = new GUITodoWizzard(this);
        cameraWizzard = new GUICamerasWizzard(this);
        channelWizzard = new GUIChannelWizzard(this);
        locationsWizzard = new GUILocationsWizzard(this);
        permissionsWizzard = new GUIPermissionWizzard(this);

        geomapWizzard = new GUIGeomapWizzard(this);

        displayPingWizzard();

        checkWindowSize();
   }

    @Override
    public void onBackPressed()
    {
        Log.d(LOGTAG, "onBackPressed:");

        boolean didwas = false;

        for (int inx = 0; inx < topframe.getChildCount(); inx++)
        {
            View plugin = topframe.getChildAt(inx);

            if ((plugin instanceof GUIPlugin) && ((GUIPlugin) plugin).isActive())
            {
                if (plugin == locationsWizzard)
                {
                    hideLocationsWizzard();
                }

                if (plugin == geomapWizzard)
                {
                    hideGeomapWizzard();
                }

                didwas = true;
                break;
            }
        }

        if (! didwas) super.onBackPressed();
    }

    public void displayCamera(boolean show, String uuid)
    {
        Camera camera = GUI.instance.onRequestCameraByUUID(uuid);
        if (camera == null) return;

        if (show)
        {
            bringToFront();

            GUIVideoSurface videoSurface = null;

            int index = -1;
            int left = 400;
            int top = 100;

            if ((videoSurface == null) && (videoSurface1 == null))
            {
                index = 0;
                videoSurface = videoSurface1 = new GUIVideoSurface(this);
            }

            if ((videoSurface == null) && (videoSurface2 == null))
            {
                index = 1;
                videoSurface = videoSurface2 = new GUIVideoSurface(this);

                left += 200;
            }

            if ((videoSurface == null) && (videoSurface3 == null))
            {
                index = 2;
                videoSurface = videoSurface3 = new GUIVideoSurface(this);
            }

            if ((videoSurface == null) && (videoSurface4 == null))
            {
                index = 3;
                videoSurface = videoSurface4 = new GUIVideoSurface(this);

                top += 200;
                left += 200;
            }

            if (index < 0) return;

            videoSurface.setPluginPositionDip(left, top);
            topframe.addView(videoSurface);

            camera.connectCamera();
            camera.registerSurface(videoSurface.getGLSVideoView());
            camera.setResolution(Camera.RESOLUTION_720P);
            camera.startRealtimeVideo();
        }

        checkWindowSize();
    }

    public void hideChannelWizzard()
    {
        hidePlugin(channelWizzard);
    }

    public void displayChannelWizzard()
    {
        showPlugin(channelWizzard);
    }

    public void hideGeomapWizzard()
    {
        hidePlugin(geomapWizzard);
    }

    public void displayGeomapWizzard(IOTObject iotobject)
    {
        geomapWizzard.setIOTObject(iotobject);

        showPlugin(geomapWizzard);
    }

    public void hideLocationsWizzard()
    {
        hidePlugin(geomapWizzard);
        hidePlugin(locationsWizzard);
    }

    public void displayLocationsWizzard()
    {
        showPlugin(locationsWizzard);
    }

    public void hidePingWizzard()
    {
        hidePlugin(pingWizzard);
    }

    public void displayPingWizzard()
    {
        showPlugin(pingWizzard);
    }

    public void hideTodoWizzard()
    {
        hidePlugin(geomapWizzard);
        hidePlugin(todoWizzard);
    }

    public void displayTodoWizzard()
    {
        showPlugin(todoWizzard);
    }

    public void hideCameraWizzard()
    {
        hidePlugin(cameraWizzard);
    }

    public void displayCameraWizzard()
    {
        showPlugin(cameraWizzard);
    }

    public void hidePermissionsWizzard()
    {
        hidePlugin(permissionsWizzard);
    }

    public void displayPermissionsWizzard()
    {
        showPlugin(permissionsWizzard);
    }

    private void showPlugin(GUIPlugin plugin)
    {
        if (plugin.getParent() == null)
        {
            topframe.addView(plugin);
        }

        checkWindowSize();
    }

    private void hidePlugin(GUIPlugin plugin)
    {
        if ((plugin != null) && (plugin.getParent() != null))
        {
            topframe.removeView(plugin);
        }
    }

    private void hideAllWizzards()
    {
        hidePlugin(todoWizzard);
        hidePlugin(pingWizzard);
        hidePlugin(cameraWizzard);
        hidePlugin(geomapWizzard);
        hidePlugin(channelWizzard);
        hidePlugin(locationsWizzard);
        hidePlugin(permissionsWizzard);
    }

    public void displaySpeechRecognition(boolean show)
    {
        if (show)
        {
            bringToFront();

            if (speechRecognition.getParent() == null)
            {
                topframe.addView(speechRecognition);
            }
        }
        else
        {
            if (speechRecognition.getParent() != null)
            {
                topframe.removeView(speechRecognition);
            }
        }

        checkWindowSize();
    }

    public void displayPinCodeMessage(int timeout)
    {
        speechRecognition.displayPinCodeMessage(timeout);
    }

    public void displayToastMessage(String message)
    {
        speechRecognition.displayToastMessage(message, 10, false);
    }

    public void displayToastMessage(String message, int seconds, boolean emphasis)
    {
        speechRecognition.displayToastMessage(message, seconds, emphasis);
    }

    private void checkWindowSize()
    {
        /*
        if ((topframe.getChildCount() == 0)
                || (speechRecognition.getParent() != null) && (topframe.getChildCount() == 1))
        {
            setWindowHeightDip(Simple.WC);
        }
        else
        {
            setWindowHeightDip(Simple.MP);
        }
        */

        if (! isActive())
        {
            startActivity(new Intent(this, getClass()));
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        GUIPlugin wizzard = null;

        if (keyCode == KeyEvent.KEYCODE_PROG_RED) wizzard = cameraWizzard;
        if (keyCode == KeyEvent.KEYCODE_PROG_GREEN) wizzard = pingWizzard;
        if (keyCode == KeyEvent.KEYCODE_PROG_YELLOW) wizzard = locationsWizzard;
        if (keyCode == KeyEvent.KEYCODE_PROG_BLUE) wizzard = channelWizzard;

        if (wizzard != null)
        {
            if (wizzard.isAttached())
            {
                hideAllWizzards();
            }
            else
            {
                hideAllWizzards();
                showPlugin(wizzard);
            }
        }

        Log.d(LOGTAG, "onKeyDown: event=" + event);

        return super.onKeyDown(keyCode, event);
    }
}