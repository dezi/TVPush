package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.base.TPL;

public class SystemsTPL extends TPL
{
    private static final String LOGTAG = SystemsTPL.class.getSimpleName();

    public SystemsTPL(Application application)
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
    public void onDeviceStatus(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceStatus:");

        Systems.iot.register.registerDeviceStatus(device);
    }
}
