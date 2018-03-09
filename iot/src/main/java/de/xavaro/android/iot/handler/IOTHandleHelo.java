package de.xavaro.android.iot.handler;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTHandler;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.simple.Json;

public class IOTHandleHelo extends IOTHandler
{
    public static void sendHELO()
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "HELO");
        Json.put(message, "human", IOT.human.toJson());
        Json.put(message, "device", IOT.device.toJson());

        IOT.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        JSONObject device = Json.getObject(message, "device");
        JSONObject human = Json.getObject(message, "human");
        JSONObject origin = Json.getObject(message, "origin");

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

        //
        // Reply with own identity.
        //

        IOTHandleMeme.sendMEME(origin);
    }
}
