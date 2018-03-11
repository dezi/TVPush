package de.xavaro.android.iot.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.comm.IOTMessageHandler;
import de.xavaro.android.iot.handler.IOTHandleHelo;
import de.xavaro.android.iot.simple.Simple;

public class IOTCloud
{
    private static final String LOGTAG = IOTCloud.class.getSimpleName();

    public static void initialize(Application appcontext)
    {
        Simple.initialize(appcontext);

        IOTBoot.initialize();

        IOTService.startService(appcontext);

        IOTMessageHandler.initialize();

        IOTHandleHelo.sendHELO();
    }

    public IOTCloud(Application appcontext)
    {
        IOT.cloud = this;

        initialize(appcontext);
    }

    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:");
    }

    public void onDeviceAlive(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceAlive:");
    }

}
