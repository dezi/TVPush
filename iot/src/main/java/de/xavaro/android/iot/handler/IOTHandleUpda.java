package de.xavaro.android.iot.handler;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTObject;

import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTThing;

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
        Log.d(LOGTAG, "onMessageReived: mess=" + Json.toPretty(message));

        JSONObject device = Json.getObject(message, "device");

        String kind = Json.getString(message, "kind");
        JSONObject data = Json.getObject(message, "data");
        if ((kind == null) || (data == null)) return;

        IOTObject newObject = null;
        IOTObject oldObject = null;

        if (kind.equals("IOTDevice"))
        {
            newObject = new IOTDevice(data);
            oldObject = IOTDevice.list.getEntry(newObject.uuid);

            int saved = oldObject.checkAndMergeContent(newObject, true, false);

            Log.d(LOGTAG, "onMessageReived: saved=" + saved);
        }
    }
}
