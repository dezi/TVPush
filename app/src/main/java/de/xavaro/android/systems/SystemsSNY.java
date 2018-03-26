package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.simple.Simple;

import zz.top.sny.base.SNY;
import zz.top.utl.Json;

public class SystemsSNY extends SNY
{
    private static final String LOGTAG = SystemsSNY.class.getSimpleName();

    public SystemsSNY(Application application)
    {
        super(application);

        GUI.instance.subSystems.registerSubsystem(getSubsystemInfo());
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:" + Json.toPretty(device));

        Systems.iot.register.registerDevice(device);
    }

    @Override
    public void onDeviceStatus(JSONObject status)
    {
        Log.d(LOGTAG, "onDeviceStatus:" + Json.toPretty(status));

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
        Log.d(LOGTAG, "onDeviceCredentials:" + Json.toPretty(credentials));

        Systems.iot.register.registerDeviceCredentials(credentials);
    }

    @Override
    public void onPincodeRequest(String uuid)
    {
        Log.d(LOGTAG, "onPincodeRequest: uuid=" + uuid);

        Simple.getHandler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Systems.gui.displayPinCodeMessage(60);
            }
        }, 1000);
    }

    @Override
    public void onBackgroundRequest()
    {
        GUI.instance.desktopActivity.sendToBack();
    }
}
