package de.xavaro.android.gui.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.gui.smart.GUIDesktopActivity;
import de.xavaro.android.gui.smart.GUISpeechListener;

import pub.android.interfaces.drv.Camera;
import pub.android.interfaces.drv.SmartPlug;
import pub.android.interfaces.gui.DesktopHandler;
import pub.android.interfaces.gui.OnSmartPlugHandlerRequest;

public class GUI implements
        DesktopHandler,
        OnSmartPlugHandlerRequest
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
        Log.d(LOGTAG, "onRequestCameraByUUID: STUB! uuid=" + uuid);

        return null;
    }

    @Override
    public SmartPlug onSmartPlugHandlerRequest(JSONObject iotDevice, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "OnSmartPlugHandlerRequest: STUB!");

        return null;
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: STUB! speech=" + speech.toString());
    }

    @Override
    public void displayCamera(boolean show, String uuid)
    {
        Log.d(LOGTAG, "displayCamera: uuid=" + uuid);

        GUI.instance.desktopActivity.displayCamera(show, uuid);
    }

    @Override
    public void displaySpeechRecognition(boolean show)
    {
        Log.d(LOGTAG, "displaySpeechRecognition: show=" + show);

        GUI.instance.desktopActivity.displaySpeechRecognition(show);
    }

    @Override
    public void displayPinCodeMessage(int timeout)
    {
        GUI.instance.desktopActivity.displayPinCodeMessage(timeout);
    }

    @Override
    public void displayToastMessage(String message, int seconds, boolean emphasis)
    {
        Log.d(LOGTAG, "displayToastMessage: message=" + message);

        GUI.instance.desktopActivity.displayToastMessage(message, seconds, emphasis);
    }
}
