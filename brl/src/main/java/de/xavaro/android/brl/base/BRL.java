package de.xavaro.android.brl.base;

import android.app.Application;

import org.json.JSONObject;

import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.all.SubSystemHandler;
import pub.android.stubs.OnInterfacesStubs;

import de.xavaro.android.brl.simple.Simple;
import de.xavaro.android.brl.simple.Json;
import de.xavaro.android.brl.R;

public class BRL extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler
{
    private static final String LOGTAG = BRL.class.getSimpleName();

    public static BRL instance;

    public BRL(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public void setInstance()
    {
        BRL.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "brl");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_brl_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_brl_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_broadlink_620));

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
}
