package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.simple.Json;

public class IOTMessage implements IOTMessageReceiver
{
    private static final String LOGTAG = IOTMessage.class.getSimpleName();

    public static void initialize()
    {
        IOT.message = new IOTMessage();

        IOTMessageService.doSubscribe(IOT.message);
    }

    public static void sendHELO()
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "HELO");
        Json.put(message, "human", IOT.human.toJson());
        Json.put(message, "device", IOT.device.toJson());

        Log.d(LOGTAG, "sendHELO: human=" + IOT.human.nick);
        Log.d(LOGTAG, "sendHELO: device=" + IOT.device.nick);

        IOTMessageService.sendMessage(message);
    }

    public static void sendMEME(IOTDevice device)
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "MEME");
        Json.put(message, "device", device.toJson());

        IOTMessageService.sendMessage(message);
    }

    @Override
    public void receiveMessage(JSONObject message)
    {
        Log.d(LOGTAG, "receiveMessage: message=" + Json.toPretty(message));
    }
}
