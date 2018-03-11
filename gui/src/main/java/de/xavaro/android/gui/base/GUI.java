package de.xavaro.android.gui.base;

import android.app.Application;
import android.util.Log;

import pub.android.interfaces.cam.Camera;
import pub.android.interfaces.gui.GraficalUserInterfaceHandler;

public class GUI implements GraficalUserInterfaceHandler
{
    private static final String LOGTAG = GUI.class.getSimpleName();

    public static GUI instance;

    public GUI(Application appcontext)
    {
        if (instance == null)
        {
            instance = this;
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
    public void displayCamera(String uuid)
    {

    }
}
