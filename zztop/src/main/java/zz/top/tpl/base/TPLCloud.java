package zz.top.tpl.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.handler.TPLHandlerSmartPlug;
import zz.top.tpl.handler.TPLHandlerSysInfo;
import zz.top.utl.Simple;

public class TPLCloud
{
    private static final String LOGTAG = TPLCloud.class.getSimpleName();

    public TPLCloud(Application context)
    {
        Simple.initialize(context);

        TPL.cloud = this;

        TPLHandlerSysInfo.sendSysInfoBroadcast();

        TPLHandlerSmartPlug.sendPlugOnOff(true);
        TPLHandlerSmartPlug.sendLEDOnOff(true);
    }

    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:");
    }
}
