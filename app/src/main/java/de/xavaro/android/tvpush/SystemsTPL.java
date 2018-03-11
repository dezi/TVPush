package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTRegister;
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

        IOTRegister.registerDevice(device);
    }

    @Override
    public void onDeviceAlive(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceAlive:");

        IOTRegister.registerDeviceAlive(device);
    }
}
