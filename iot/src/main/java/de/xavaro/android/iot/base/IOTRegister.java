package de.xavaro.android.iot.base;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Simple;

public class IOTRegister
{
    private final static String LOGTAG = IOTRegister.class.getSimpleName();

    public static void registerDevice(JSONObject register)
    {
        JSONObject device = Json.getObject(register, "device");
        JSONObject credentials = Json.getObject(register, "credentials");
        JSONObject network = Json.getObject(register, "network");

        String caps = Json.getString(device, "capabilities");

        Log.d(LOGTAG, "registerDevice: uuid=" + Json.getString(device, "uuid"));
        Log.d(LOGTAG, "registerDevice: name=" + Json.getString(device, "name"));
        Log.d(LOGTAG, "registerDevice: model=" + Json.getString(device, "model"));
        Log.d(LOGTAG, "registerDevice: capabilities=" + caps);

        Log.d(LOGTAG, "registerDevice:"
                + " p2p_id=" + Json.getString(credentials, "p2p_id")
                + " p2p_pw=" + Json.getString(credentials, "p2p_pw"));

        JSONArray capabilities = Json.jsonArrayFromSeparatedString(Simple.getDeviceCapabilities(), "\\|");

        Json.put(device, "capabilities", capabilities);

        IOTDevice newDevice = new IOTDevice(device);

        IOTDevices.addEntry(newDevice, false);
    }
}