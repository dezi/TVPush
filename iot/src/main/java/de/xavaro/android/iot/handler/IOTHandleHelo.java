package de.xavaro.android.iot.handler;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;

public class IOTHandleHelo extends IOTHandle
{
    public static void sendHELO()
    {
        if (IOT.device != null)
        {
            JSONObject message = new JSONObject();

            Json.put(message, "type", "HELO");
            Json.put(message, "device", IOT.device.toJson());

            if (IOT.human != null)
            {
                Json.put(message, "human", IOT.human.toJson());
            }

            IOT.message.sendMessage(message);
        }
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
