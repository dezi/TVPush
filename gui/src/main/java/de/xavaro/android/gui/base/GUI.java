package de.xavaro.android.gui.base;

import android.app.Application;
import android.util.Log;

import pub.android.interfaces.cam.Camera;
import pub.android.interfaces.gui.GraficalUserInterfaceHandler;

public class GUI implements GraficalUserInterfaceHandler
{
    private static final String LOGTAG = GUI.class.getSimpleName();

    public static GUI instance;

    public GUIApplication application;

    public GUI(Application application)
    {
        if (instance == null)
        {
            instance = this;

            if (application instanceof GUIApplication)
            {
                this.application = (GUIApplication) application;
            }
            else
            {
                throw new RuntimeException("Application does not extend GUIApplication.");
            }
        }
        else
        {
            throw new RuntimeException("GUI system already initialized.");
        }
    }

    @Override
    public Camera onRequestCameraByUUID(String uuid)
    {
        Log.d(LOGTAG, "onRequestCameraByUUID: uuid=" + uuid);

        return null;
    }

    @Override
    public void displaySpeechRecognition(boolean show)
    {
        Log.d(LOGTAG, "displaySpeechRecognition: show=" + show);
    }

    @Override
    public void displayCamera(String uuid)
    {
        Log.d(LOGTAG, "displayCamera: uuid=" + uuid);
    }
}
