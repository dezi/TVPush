package de.xavaro.android.iot.handler;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;

public class IOTHandleStot extends IOTHandle
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
        final JSONObject speech = Json.getObject(message, "speech");
        JSONArray results = Json.getArray(speech, "results");

        if ((results == null) || (results.length() == 0))
        {
            //
            // Bogus message.
            //

            return;
        }

        JSONObject result = Json.getObject(results, 0);
        String text = Json.getString(result, "text");
        float conf = Json.getFloat(result, "conf");

        //
        // Just for logging this message. We do not need it.
        //

        Log.d(LOGTAG, "receiveSTOT: conf=" + conf + " text=" + text);

        if (Simple.isTV())
        {
            Simple.getHandler().post(new Runnable()
            {
                @Override
                public void run()
                {
                    IOT.instance.onSpeechResults(speech);
                }
            });
        }
    }
}
