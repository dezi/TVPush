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
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;

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

    public void initializeBasicSubscribers()
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

    public static void sendHELO()
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "HELO");
        Json.put(message, "human", IOT.human.toJson());
        Json.put(message, "device", IOT.device.toJson());

        Log.d(LOGTAG, "sendHELO: human=" + IOT.human.nick);
        Log.d(LOGTAG, "sendHELO: device=" + IOT.device.nick);

        sendMessage(message);
    }

    public static void sendMEME(JSONObject destination)
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "MEME");
        Json.put(message, "human", IOT.human.toJson());
        Json.put(message, "device", IOT.device.toJson());
        Json.put(message, "destination", destination);

        Log.d(LOGTAG, "sendMEME: human=" + IOT.human.nick);
        Log.d(LOGTAG, "sendMEME: device=" + IOT.device.nick);

        sendMessage(message);
    }

    public static void sendSTOT(JSONObject speech)
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "STOT");
        Json.put(message, "speech", speech);

        Log.d(LOGTAG, "sendSTOT: device=" + IOT.device.nick);

        sendMessage(message);
    }

    public static void sendMessage(JSONObject message)
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

        if (type.equals("HELO")) receiveHELO(message);
        if (type.equals("MEME")) receiveMEME(message);
        if (type.equals("STOT")) receiveSTOT(message);
    }

    private void receiveHELO(JSONObject message)
    {

        JSONObject human = Json.getObject(message, "human");
        IOTHuman.checkAndMergeContent(human, true);

        JSONObject device = Json.getObject(message, "device");
        IOTDevice.checkAndMergeContent(device, true);

        JSONObject origin = Json.getObject(message, "origin");
        sendMEME(origin);
    }

    private void receiveMEME(JSONObject message)
    {
        JSONObject human = Json.getObject(message, "human");
        IOTHuman.checkAndMergeContent(human, true);

        JSONObject device = Json.getObject(message, "device");
        IOTDevice.checkAndMergeContent(device, true);
    }

    private void receiveSTOT(JSONObject message)
    {
        JSONObject speech = Json.getObject(message, "speech");
        JSONArray results = Json.getArray(speech, "results");
        if (results == null) return;

        Log.d(LOGTAG, "receiveSTOT: words=" + results.length());
    }
}
