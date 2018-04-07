package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.brl.base.BRL;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;

public class SystemsBRL extends BRL
{
    private static final String LOGTAG = SystemsBRL.class.getSimpleName();

    public SystemsBRL(Application application)
    {
        super(application);
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        return GUI.instance.subSystems.getSubsystemState(subsystem);
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        GUI.instance.subSystems.setSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        GUI.instance.subSystems.setSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:");

        IOT.instance.register.registerDevice(device);
    }

    @Override
    public void onDeviceStatus(JSONObject status)
    {
        Log.d(LOGTAG, "onDeviceStatus:");

        IOT.instance.register.registerDeviceStatus(status);
    }

    @Override
    public void onDeviceMetadata(JSONObject metatdata)
    {
        Log.d(LOGTAG, "onDeviceMetadata:");

        IOT.instance.register.registerDeviceMetadata(metatdata);
    }

    @Override
    public void onDeviceCredentials(JSONObject credentials)
    {
        Log.d(LOGTAG, "onDeviceCredentials:");

        IOT.instance.register.registerDeviceCredentials(credentials);
    }
}
