package zz.top.tpl.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.comm.TPLMessageHandler;
import zz.top.tpl.comm.TPLMessageService;
import zz.top.tpl.handler.TPLHandlerSysInfo;
import zz.top.utl.Simple;

import pub.android.interfaces.iot.InternetOfThingsHandler;

public class TPL implements InternetOfThingsHandler
{
    private static final String LOGTAG = TPL.class.getSimpleName();

    public static TPL instance;

    public TPLMessageHandler message;

    public TPL(Application application)
    {
        if (instance == null)
        {
            instance = this;

            Simple.initialize(application);

            TPLMessageHandler.initialize();
            TPLMessageService.startService();

            TPLHandlerSysInfo.sendSysInfoBroadcast();
        }
        else
        {
            throw new RuntimeException("TPL system already initialized.");
        }
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound: STUB!");
    }

    @Override
    public void onDeviceAlive(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceAlive: STUB!");
    }

    @Override
    public boolean switchLED(String uuid, boolean onoff)
    {
        Log.d(LOGTAG, "switchLED: uuid=" + uuid + " onoff=" + onoff);

        return false;
    }
}
