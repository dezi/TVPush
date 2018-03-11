package de.xavaro.android.iot.handler;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.gui.simple.Json;

public class IOTHandleMeme extends IOTHandle
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
        JSONObject device = Json.getObject(message, "device");

        //
        // Check if message comes from ourself via
        // broadcast feed back. If so, ignore.
        //

        if (IOTSimple.equals(Json.getString(device, "uuid"), IOT.device.uuid))
        {
            //
            // HELO from our identity, ignore.
            //

            return;
        }

        //
        // Collect external device and human.
        //

        IOTHuman.checkAndMergeContent(human, true);
        IOTDevice.checkAndMergeContent(device, true);
    }
}
