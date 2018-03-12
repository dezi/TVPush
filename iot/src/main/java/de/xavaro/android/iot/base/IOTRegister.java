package de.xavaro.android.iot.base;

import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class IOTRegister
{
    private final static String LOGTAG = IOTRegister.class.getSimpleName();

    private final Map<String, String> ipcache = new HashMap<>();

    public void registerDevice(JSONObject register)
    {
        JSONObject device = Json.getObject(register, "device");
        JSONObject credentials = Json.getObject(register, "credentials");
        JSONObject network = Json.getObject(register, "network");

        String caps = Json.getString(device, "capabilities");

        Log.d(LOGTAG, "registerDevice: uuid=" + Json.getString(device, "uuid"));
        Log.d(LOGTAG, "registerDevice: name=" + Json.getString(device, "name"));
        Log.d(LOGTAG, "registerDevice: model=" + Json.getString(device, "model"));
        Log.d(LOGTAG, "registerDevice: capabilities=" + caps);

        if (Json.has(credentials, "p2p_id"))
        {
            Log.d(LOGTAG, "registerDevice:"
                    + " p2p_id=" + Json.getString(credentials, "p2p_id")
                    + " p2p_pw=" + Json.getString(credentials, "p2p_pw"));
        }

        JSONArray capabilities = Json.jsonArrayFromSeparatedString(caps, "\\|");
        Json.put(device, "capabilities", capabilities);

        IOTDevice newDevice = new IOTDevice(device);

        IOTDevices.addEntry(newDevice, false);
    }

    public void registerDeviceAlive(JSONObject register)
    {
        JSONObject device = Json.getObject(register, "device");
        JSONObject network = Json.getObject(register, "network");

        String uuid = Json.getString(device, "uuid");
        String ipaddr = Json.getString(network, "ipaddr");

        if (uuid == null) uuid = ipcache.get(ipaddr);

        if ((uuid != null) && (ipaddr != null))
        {
            ipcache.put(ipaddr, uuid);

            Log.e(LOGTAG, "registerDeviceAlive: uuid=" + uuid + " ipaddr=" + ipaddr);

            Json.put(network, "uuid", uuid);

            IOTStatus newDevice = new IOTStatus(network);

            IOTStatusses.addEntry(newDevice, false);
        }
        else
        {
            Log.d(LOGTAG, "registerDeviceAlive: no device.");
        }
    }
}
