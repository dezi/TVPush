package de.xavaro.android.iot.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.comm.IOTMessageHandler;
import de.xavaro.android.iot.handler.IOTHandleHelo;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.simple.Simple;

public class IOT
{
    private static final String LOGTAG = IOT.class.getSimpleName();

    public static IOT instance;

    public static IOTMeme meme;
    public static IOTHuman human;
    public static IOTDevice device;

    public static IOTMessageHandler message;

    public IOT(Application appcontext)
    {
        IOT.instance = this;

        Simple.initialize(appcontext);

        IOTBoot.initialize();

        IOTService.startService(appcontext);

        IOTMessageHandler.initialize();

        IOTHandleHelo.sendHELO();
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
