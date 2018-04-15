package de.xavaro.android.cam.base;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.cam.R;
import de.xavaro.android.cam.simple.Json;
import de.xavaro.android.cam.simple.Simple;
import de.xavaro.android.pub.interfaces.all.SubSystemHandler;
import de.xavaro.android.pub.interfaces.ext.OnDeviceHandler;
import de.xavaro.android.pub.stubs.OnInterfacesStubs;

public class CAM extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler
{
    private static final String LOGTAG = CAM.class.getSimpleName();

    public static CAM instance;

    public Application appcontext;

    public CAM(Application application)
    {
        appcontext = application;

        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        CAM.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        int mode = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                ? SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY
                : SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE
                ;

        JSONObject info = new JSONObject();

        Json.put(info, "drv", "cam");
        Json.put(info, "mode", mode);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_cam_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_cam_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_cam_520));

        return info;
    }

    @Override
    public JSONObject getSubsystemSettings()
    {
        return getSubsystemInfo();
    }

    @Override
    public void startSubsystem(String subsystem)
    {
        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    //endregion SubSystemHandler
}
