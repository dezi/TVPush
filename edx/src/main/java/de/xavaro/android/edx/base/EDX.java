package de.xavaro.android.edx.base;


import android.app.Application;

import org.json.JSONObject;

import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.all.SubSystemHandler;
import pub.android.stubs.OnInterfacesStubs;

import de.xavaro.android.edx.comm.EDXDiscover;

import de.xavaro.android.edx.simple.Simple;
import de.xavaro.android.edx.simple.Json;
import de.xavaro.android.edx.R;

public class EDX extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler
{
    private static final String LOGTAG = EDX.class.getSimpleName();

    public static EDX instance;

    public EDXDiscover discover;

    public EDX(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public void setInstance()
    {
        EDX.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "edx");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_edx_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_edx_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_edimax_550));

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
        EDXDiscover.startService();

        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        EDXDiscover.stopService();

        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }
}
