package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;

import zz.top.tpl.base.TPL;

public class SystemsTPL extends TPL
{
    private static final String LOGTAG = SystemsTPL.class.getSimpleName();

    public SystemsTPL(Application application)
    {
        super(application);

        GUI.instance.subSystems.registerSubsystem(getSubsystemInfo());
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:");

        Systems.iot.register.registerDevice(device);
    }

    @Override
    public void onDeviceStatus(JSONObject status)
    {
        Log.d(LOGTAG, "onDeviceStatus:");

        Systems.iot.register.registerDeviceStatus(status);
    }

    @Override
    public void onDeviceMetadata(JSONObject metatdata)
    {
        Log.d(LOGTAG, "onDeviceMetadata:");

        Systems.iot.register.registerDeviceMetadata(metatdata);
    }

    @Override
    public void onDeviceCredentials(JSONObject credentials)
    {
        Log.d(LOGTAG, "onDeviceCredentials:");

        Systems.iot.register.registerDeviceCredentials(credentials);
    }
}
