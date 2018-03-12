package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTRegister;

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
    public void onDeviceAlive(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceAlive:");

        Systems.iot.register.registerDeviceAlive(device);
    }
}
