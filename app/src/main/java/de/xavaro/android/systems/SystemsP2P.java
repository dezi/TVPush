package de.xavaro.android.systems;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import zz.top.p2p.base.P2P;

public class SystemsP2P extends P2P
{
    private static final String LOGTAG = SystemsP2P.class.getSimpleName();

    public SystemsP2P(Application application)
    {
        super(application);
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
