package de.xavaro.android.iot.handler;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;

public class IOTHandleHelo extends IOTHandle
{
    public static void sendHELO()
    {
        if (IOT.device != null)
        {
            JSONObject message = new JSONObject();

            Json.put(message, "type", "HELO");
            Json.put(message, "device", IOT.device.toJson());

            IOT.message.sendMessage(message);
        }
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        JSONObject device = Json.getObject(message, "device");
        JSONObject origin = Json.getObject(message, "origin");

        //
        // Collect external device.
        //

        IOTDevice newDevice = new IOTDevice(device);

        if (IOTDevice.list.addEntry(newDevice, true, false) >= 0)
        {
            //
            // Collect status.
            //

            IOTStatus newStatus = new IOTStatus(newDevice.uuid);

            newStatus.ipaddr = Json.getString(origin, "ipaddr");
            newStatus.ipport = Json.getInt(origin, "ipport");

            if (IOTStatus.list.addEntry(newStatus, false, false) >= 0)
            {
                //
                // Reply with own identity.
                //

                IOTHandleMeme.sendMEME(origin);
            }
        }
    }
}
