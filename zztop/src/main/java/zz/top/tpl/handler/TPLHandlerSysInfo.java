package zz.top.tpl.handler;

import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.base.TPL;
import zz.top.utl.Json;

public class TPLHandlerSysInfo extends TPLHandler
{
    private static final String LOGTAG = TPLHandlerSysInfo.class.getSimpleName();

    public static void sendSysInfoBroadcast()
    {
        String mess = "{\"system\":{\"get_sysinfo\":{}}}";

        JSONObject message = Json.fromStringObject(mess);

        TPL.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        Log.d(LOGTAG, Json.toPretty(message));
    }
}
