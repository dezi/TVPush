package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.bcn.base.BCN;

public class SystemsBCN extends BCN
{
    private static final String LOGTAG = SystemsBCN.class.getSimpleName();

    public SystemsBCN(Application application)
    {
        super(application);

        GUI.instance.subSystems.registerSubsystem(getSubsystemInfo());
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        GUI.instance.subSystems.registerSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        GUI.instance.subSystems.registerSubsystemRunstate(subsystem, state);
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
}
