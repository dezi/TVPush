package de.xavaro.android.iot.handler;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTHandler;

import de.xavaro.android.simple.Json;

public class IOTHandleStot extends IOTHandler
{
    private static final String LOGTAG = IOTHandleStot.class.getSimpleName();

    public static void sendSTOT(JSONObject speech)
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "STOT");
        Json.put(message, "speech", speech);

        IOT.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        JSONObject speech = Json.getObject(message, "speech");
        JSONArray results = Json.getArray(speech, "results");
        if (results == null) return;

        Log.d(LOGTAG, "receiveSTOT: words=" + results.length());
    }
}
