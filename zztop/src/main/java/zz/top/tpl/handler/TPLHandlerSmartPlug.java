package zz.top.tpl.handler;

import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.base.TPL;
import zz.top.utl.Json;

public class TPLHandlerSmartPlug extends TPLHandler
{
    private static final String LOGTAG = TPLHandlerSmartPlug.class.getSimpleName();

    public static void sendPlugOnOff(boolean on)
    {
        String messOn = "{\"system\":{\"set_relay_state\":{\"state\":1}}}";
        String messOff = "{\"system\":{\"set_relay_state\":{\"state\":0}}}";

        JSONObject message = Json.fromStringObject(on ? messOn : messOff);

        TPL.message.sendMessage(message);
    }

    public static void sendLEDOnOff(boolean on)
    {
        String messOn = "{\"system\":{\"set_led_off\":{\"off\": 1}}}";
        String messOff = "{\"system\":{\"set_led_off\":{\"off\": 0}}}";

        JSONObject message = Json.fromStringObject(on ? messOn : messOff);

        TPL.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        Log.d(LOGTAG, Json.toPretty(message));
    }
}
