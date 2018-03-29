package de.xavaro.android.iot.handler;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTObject;

import de.xavaro.android.iot.things.IOTDevice;

import de.xavaro.android.iot.simple.Json;

public class IOTHandleUpda extends IOTHandle
{
    private static final String LOGTAG = IOTHandleUpda.class.getSimpleName();

    public static void sendUPDA(IOTObject iotObject)
    {
        if (iotObject != null)
        {
            JSONObject message = new JSONObject();

            Json.put(message, "type", "UPDA");
            Json.put(message, "kind", iotObject.getClass().getSimpleName());
            Json.put(message, "data", iotObject.toJson());

            IOT.message.sendMessage(message);
        }
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        String kind = Json.getString(message, "kind");
        JSONObject data = Json.getObject(message, "data");
        if ((kind == null) || (data == null)) return;

        int saved = IOTDefs.IOT_SAVE_FAILED;

        if (kind.equals("IOTDevice"))
        {
            saved = IOTDevice.list.addEntry(new IOTDevice(data), true, false);
        }

        Log.d(LOGTAG, "onMessageReived: saved=" + saved);
    }
}
