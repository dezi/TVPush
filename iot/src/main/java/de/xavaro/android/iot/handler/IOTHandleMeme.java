package de.xavaro.android.iot.handler;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTHandler;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.simple.Json;

public class IOTHandleMeme extends IOTHandler
{
    public static void sendMEME(JSONObject destination)
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "MEME");
        Json.put(message, "human", IOT.human.toJson());
        Json.put(message, "device", IOT.device.toJson());
        Json.put(message, "destination", destination);

        IOT.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        JSONObject human = Json.getObject(message, "human");
        IOTHuman.checkAndMergeContent(human, true);

        JSONObject device = Json.getObject(message, "device");
        IOTDevice.checkAndMergeContent(device, true);
    }
}
