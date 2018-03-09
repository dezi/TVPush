package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTHandler;
import de.xavaro.android.iot.handler.IOTHandleHelo;
import de.xavaro.android.iot.handler.IOTHandleMeme;
import de.xavaro.android.iot.handler.IOTHandleStot;

import de.xavaro.android.simple.Json;

public class IOTMessageHandler
{
    private static final String LOGTAG = IOTMessageHandler.class.getSimpleName();

    public static void initialize()
    {
        IOT.message = new IOTMessageHandler();
        IOT.message.initializeBasicSubscribers();
    }

    private final Map<String, ArrayList<IOTHandler>> subscribers = new HashMap<>();

    private void initializeBasicSubscribers()
    {
        subscribe("HELO", new IOTHandleHelo());
        subscribe("MEME", new IOTHandleMeme());
        subscribe("STOT", new IOTHandleStot());
    }

    public void subscribe(String type, IOTHandler handler)
    {
        ArrayList<IOTHandler> typeHandlers = subscribers.get(type);

        if (typeHandlers == null)
        {
            typeHandlers = new ArrayList<>();
            subscribers.put(type, typeHandlers);
        }

        if (! typeHandlers.contains(handler))
        {
            typeHandlers.add(handler);
        }
    }

    public void sendMessage(JSONObject message)
    {
        Json.put(message, "uuid", UUID.randomUUID().toString());
        Json.put(message, "time", System.currentTimeMillis());

        if (! Json.has(message, "device"))
        {
            JSONObject device = new JSONObject();
            Json.put(message, "device", device);

            Json.put(device, "uuid", IOT.device.uuid);
        }

        IOTMessageService.sendMessage(message);
    }

    public void receiveMessage(JSONObject message)
    {
        String type = Json.getString(message, "type");

        JSONObject device = Json.getObject(message, "device");
        JSONObject origin = Json.getObject(message, "origin");
        String deviceUUID = Json.getString(device, "uuid");

        if (type == null) return;
        if (deviceUUID == null) return;

        if (deviceUUID.equals(IOT.device.uuid))
        {
            //
            // Broadcasted message from ourself.
            //

            return;
        }

        Log.d(LOGTAG, "receiveMessage: message"
                + " type=" + type
                + " ipaddr=" + Json.getString(origin, "ipaddr"));

        //
        // Call type handlers.
        //

        ArrayList<IOTHandler> typeHandlers = subscribers.get(type);
        if (typeHandlers == null) return;

        for (IOTHandler typeHandler : typeHandlers)
        {
            typeHandler.onMessageReived(message);
        }
    }
}
