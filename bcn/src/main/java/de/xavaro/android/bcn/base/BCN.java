package de.xavaro.android.bcn.base;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.bcn.simple.Simple;
import de.xavaro.android.bcn.simple.Json;
import de.xavaro.android.bcn.R;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.stubs.OnInterfacesStubs;

public class BCN extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler
{
    private static final String LOGTAG = BCN.class.getSimpleName();

    public static BCN instance;

    public BCN(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "bcn");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_bcn_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_bcn_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_beacon_220));

        return info;
    }

    @Override
    public void startSubsystem()
    {
        onSubsystemStarted("bcn", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem()
    {
        onSubsystemStopped("bcn", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }
}
