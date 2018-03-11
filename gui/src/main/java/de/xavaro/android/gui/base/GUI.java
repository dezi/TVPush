package de.xavaro.android.gui.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.gui.smart.GUIDesktopActivity;
import de.xavaro.android.gui.smart.GUISpeechListener;
import pub.android.interfaces.cam.Camera;
import pub.android.interfaces.gui.GraficalUserInterfaceHandler;

public class GUI implements GraficalUserInterfaceHandler
{
    private static final String LOGTAG = GUI.class.getSimpleName();

    public static GUI instance;

    public GUIApplication application;

    public GUISpeechListener speechListener;
    public GUIDesktopActivity desktopActivity;

    public GUI(Application application)
    {
        if (instance == null)
        {
            instance = this;

            if (application instanceof GUIApplication)
            {
                this.application = (GUIApplication) application;

                this.speechListener = new GUISpeechListener(application);
                this.speechListener.startListening();
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
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: speech=" + speech.toString());
    }

    @Override
    public void displaySpeechRecognition(boolean show)
    {
        Log.d(LOGTAG, "displaySpeechRecognition: show=" + show);

        GUI.instance.desktopActivity.displaySpeechRecognition(show);
    }

    @Override
    public void displayCamera(String uuid)
    {
        Log.d(LOGTAG, "displayCamera: uuid=" + uuid);
    }
}
