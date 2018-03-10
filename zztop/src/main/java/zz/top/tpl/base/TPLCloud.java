package zz.top.tpl.base;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.comm.TPLUDPReceiver;
import zz.top.tpl.handler.TPLHandlerSysInfo;

public class TPLCloud
{
    private static final String LOGTAG = TPLCloud.class.getSimpleName();

    public TPLCloud(Context context)
    {
        TPL.cloud = this;

        TPLHandlerSysInfo.sendSysInfoBroadcast();
    }

    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:");
    }
}
