package de.xavaro.android.iot.comm;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTHuman;

import de.xavaro.android.simple.Json;

public class IOTMessage implements IOTMessageReceiver
{
    private static final String LOGTAG = IOTMessage.class.getSimpleName();

    public static void initialize()
    {
        IOT.message = new IOTMessage();

        IOTMessageService.doSubscribe(IOT.message);
    }

    public static void sendHELO()
    {
        JSONObject message = new JSONObject();

        Json.put(message, "type", "HELO");
        Json.put(message, "human", IOT.human.toJson());
        Json.put(message, "device", IOT.device.toJson());

        Log.d(LOGTAG, "sendHELO: human=" + IOT.human.nick);
        Log.d(LOGTAG, "sendHELO: device=" + IOT.device.nick);

        IOTMessageService.sendMessage(message);
    }

    public static void sendSTOT(JSONObject speech)
    {
        JSONObject deviceShort = new JSONObject();
        Json.put(deviceShort, "uuid", IOT.device.uuid);

        JSONObject message = new JSONObject();

        Json.put(message, "type", "STOT");
        Json.put(message, "device", deviceShort);
        Json.put(message, "speech", speech);

        Log.d(LOGTAG, "sendSTOT: device=" + IOT.device.nick);

        IOTMessageService.sendMessage(message);
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

        IOTMessageService.sendMessage(message);
    }

    @Override
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
