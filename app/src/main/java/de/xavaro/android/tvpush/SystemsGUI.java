package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import de.xavaro.android.gui.base.GUI;

import pub.android.interfaces.cam.Camera;

public class SystemsGUI extends GUI
{
    private static final String LOGTAG = SystemsGUI.class.getSimpleName();

    public SystemsGUI(Application application)
    {
        super(application);
    }

    @Override
    public Camera onRequestCameraByUUID(String uuid)
    {
        Log.d(LOGTAG, "onRequestCameraByUUID: uuid=" + uuid);

        return null;
    }
}
