package de.xavaro.android.gui.smart;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIActivity;
import de.xavaro.android.gui.plugin.GUIChannelWizzard;
import de.xavaro.android.gui.plugin.GUISpeechRecogniton;
import de.xavaro.android.gui.plugin.GUIVideoSurface;
import de.xavaro.android.gui.simple.Simple;

import pub.android.interfaces.cam.Camera;

public class GUIDesktopActivity extends GUIActivity
{
    private final static String LOGTAG = GUIDesktopActivity.class.getSimpleName();

    public GUISpeechRecogniton speechRecognition;
    public GUIChannelWizzard channelWizzard;

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

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(Simple.MP, Simple.WC, Gravity.BOTTOM);
        topframe.addView(speechRecognition, lp);

        channelWizzard = new GUIChannelWizzard(this);
        channelWizzard.setPosition(100,100);
        channelWizzard.setSizeDip(600,800);

        topframe.addView(channelWizzard);

        checkWindowSize();
    }

    @Override
    public void onBackPressed()
    {
        Log.d(LOGTAG, "onBackPressed:");

        super.onBackPressed();
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

            videoSurface.setPosition(margin, margin + index * (videoSurface.getPluginHeight() + margin));
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