package de.xavaro.android.iot.handler;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.things.IOTDevices;

public class IOTHandleMeme extends IOTHandle
{
    public static void sendMEME(JSONObject destination)
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "MEME");
        Json.put(message, "device", IOT.device.toJson());

        Json.put(message, "destination", destination);

        IOT.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        JSONObject device = Json.getObject(message, "device");
        JSONObject origin = Json.getObject(message, "origin");

        //
        // Check if message comes from ourself via
        // broadcast feed back. If so, ignore.
        //

        if (IOTSimple.equals(Json.getString(device, "uuid"), IOT.device.uuid))
        {
            //
            // MEME from our identity, ignore.
            //

            return;
        }

        //
        // Collect external device.
        //

        IOTDevice newDevice = new IOTDevice(device);

        if (IOTDevices.addEntry(newDevice, true) >= 0)
        {
            //
            // Collect status.
            //

            IOTStatus newStatus = new IOTStatus(newDevice.uuid);

            newStatus.ipaddr = Json.getString(origin, "ipaddr");
            newStatus.ipport = Json.getInt(origin, "ipport");

            if (IOTStatusses.addEntry(newStatus, false) >= 0)
            {
                //
                // Reply with own identity.
                //

                IOTHandleMeme.sendMEME(origin);
            }
        }
    }
}
