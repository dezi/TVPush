package de.xavaro.android.awx.base;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.ext.GetDevicesRequest;
import de.xavaro.android.pub.interfaces.ext.OnDeviceHandler;
import de.xavaro.android.pub.interfaces.all.SubSystemHandler;

import de.xavaro.android.pub.stubs.OnInterfacesStubs;

import de.xavaro.android.awx.comm.AWXDiscover;
import de.xavaro.android.awx.simple.Simple;
import de.xavaro.android.awx.simple.Json;
import de.xavaro.android.awx.R;

public class AWX extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDevicesRequest
{
    private static final String LOGTAG = AWX.class.getSimpleName();

    public static AWX instance;

    public AWXDiscover discover;

    public AWX(Application application)
    {
        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        AWX.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "awx");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_awx_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_awx_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_awox_600));

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
        AWXDiscover.startService();

        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        AWXDiscover.stopService();

        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    //endregion SubSystemHandler
}
