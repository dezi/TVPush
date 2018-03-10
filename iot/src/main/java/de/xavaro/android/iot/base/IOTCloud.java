package de.xavaro.android.iot.base;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.comm.IOTMessageHandler;
import de.xavaro.android.iot.handler.IOTHandleHelo;
import zz.top.tpl.base.TPL;
import zz.top.tpl.base.TPLCloud;
import zz.top.tpl.handler.TPLHandlerSysInfo;
import zz.top.utl.Simple;

public class IOTCloud
{
    private static final String LOGTAG = TPLCloud.class.getSimpleName();

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
