package de.xavaro.android.systems;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import zz.top.sny.base.SNY;
import zz.top.utl.Json;

public class SystemsSNY extends SNY
{
    private static final String LOGTAG = SystemsSNY.class.getSimpleName();

    public SystemsSNY(Application application)
    {
        super(application);
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
    public void onDeviceCredentials(JSONObject credentials)
    {
        Log.d(LOGTAG, "onDeviceCredentials:");

        Systems.iot.register.registerDeviceStatus(credentials);
    }
}
