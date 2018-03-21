package de.xavaro.android.gui.smart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIActivity;
import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.wizzards.GUIChannelWizzard;
import de.xavaro.android.gui.wizzards.GUIGeomapWizzard;
import de.xavaro.android.gui.plugin.GUISpeechRecogniton;
import de.xavaro.android.gui.wizzards.GUILocationsWizzard;
import de.xavaro.android.gui.wizzards.GUIPermissionWizzard;
import de.xavaro.android.gui.wizzards.GUITodoWizzard;
import de.xavaro.android.gui.plugin.GUIVideoSurface;
import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.base.IOTObject;
import pub.android.interfaces.cam.Camera;

public class GUIDesktopActivity extends GUIActivity
{
    private final static String LOGTAG = GUIDesktopActivity.class.getSimpleName();

    public GUISpeechRecogniton speechRecognition;
    public GUILocationsWizzard locationsWizzard;
    public GUIGeomapWizzard geomapWizzard;

    public GUIChannelWizzard channelWizzard;
    public GUIPermissionWizzard permissionsWizzard;
    public GUITodoWizzard todoWizzard;

    private GUIVideoSurface videoSurface1;
    private GUIVideoSurface videoSurface2;
    private GUIVideoSurface videoSurface3;
    private GUIVideoSurface videoSurface4;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        GUI.instance.desktopActivity = this;

        speechRecognition = new GUISpeechRecogniton(this);

        topframe.addView(speechRecognition, speechRecognition.getPreferredLayout());

        geomapWizzard = new GUIGeomapWizzard(this);

        channelWizzard = new GUIChannelWizzard(this);
        //topframe.addView(channelWizzard);

        todoWizzard = new GUITodoWizzard(this);
        //topframe.addView(todoWizzard);

        permissionsWizzard = new GUIPermissionWizzard(this);
        //topframe.addView(permissionsWizzard);

        locationsWizzard = new GUILocationsWizzard(this);
        topframe.addView(locationsWizzard);

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
                topframe.removeView(plugin);
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
            int margin = 40;

            if ((videoSurface == null) && (videoSurface1 == null))
            {
                index = 0;
                videoSurface = videoSurface1 = new GUIVideoSurface(this);
            }

            if ((videoSurface == null) && (videoSurface2 == null))
            {
                index = 1;
                videoSurface = videoSurface2 = new GUIVideoSurface(this);
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
            }

            if (index < 0) return;

            videoSurface.setPluginPositionDip(margin, margin + index * (videoSurface.getPluginHeight() + margin));
            topframe.addView(videoSurface);

            camera.connectCamera();
            camera.registerSurface(videoSurface.getGLSVideoView());
            camera.setResolution(Camera.RESOLUTION_720P);
            camera.startRealtimeVideo();
        }

        checkWindowSize();
    }

    public void displayChannelWizzard(boolean show)
    {
        if (show)
        {
            bringToFront();

            if (channelWizzard.getParent() == null)
            {
                topframe.addView(channelWizzard);
            }
        }
        else
        {
            if (channelWizzard.getParent() != null)
            {
                topframe.removeView(channelWizzard);
            }
        }

        checkWindowSize();
    }

    public void hideGeomapWizzard()
    {
        if ((geomapWizzard != null) && (geomapWizzard.getParent() != null))
        {
            topframe.removeView(geomapWizzard);
        }

        geomapWizzard = null;
    }

    public void displayGeomapWizzard(IOTObject iotobject)
    {
        if (geomapWizzard == null)
        {
            geomapWizzard = new GUIGeomapWizzard(this);
        }

        if (geomapWizzard.getParent() == null)
        {
            topframe.addView(geomapWizzard);
        }

        geomapWizzard.setIOTObject(iotobject);

        checkWindowSize();
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
        if ((topframe.getChildCount() == 0)
                || (speechRecognition.getParent() != null) && (topframe.getChildCount() == 1))
        {
            setWindowHeightDip(Simple.WC);
        }
        else
        {
            setWindowHeightDip(Simple.MP);
        }

        if (! isActive())
        {
            startActivity(new Intent(this, getClass()));
        }
    }
}