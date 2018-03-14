package de.xavaro.android.iot.base;

import android.util.Log;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTCredentials;
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

        String uuid = Json.getString(device, "uuid");
        String name = Json.getString(device, "name");
        String model = Json.getString(device, "model");
        String caps = Json.getString(device, "capabilities");

        Log.d(LOGTAG, "registerDevice: uuid=" + uuid);
        Log.d(LOGTAG, "registerDevice: name=" + name);
        Log.d(LOGTAG, "registerDevice: model=" + model);
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

        if (credentials != null)
        {
            JSONObject crendital = new JSONObject();

            Json.put(crendital, "uuid", uuid);
            Json.put(crendital, "credentials", credentials);

            IOTCredential newCredential = new IOTCredential(crendital);

            IOTCredentials.addEntry(newCredential, false);
        }
    }

    public void registerDeviceStatus(JSONObject status)
    {
        String uuid = Json.getString(status, "uuid");
        String ipaddr = Json.getString(status, "ipaddr");

        if (uuid == null)
        {
            uuid = ipcache.get(ipaddr);

            if (uuid != null) Json.put(status, "uuid", uuid);
        }

        if ((uuid != null) && (ipaddr != null))
        {
            ipcache.put(ipaddr, uuid);

            Log.d(LOGTAG, "registerDeviceStatus: uuid=" + uuid + " ipaddr=" + ipaddr);

            IOTStatus newStatus = new IOTStatus(status);

            IOTStatusses.addEntry(newStatus, false);
        }
        else
        {
            Log.d(LOGTAG, "registerDeviceStatus: no device.");
        }
    }

    public void registerDeviceCredentials(JSONObject credentials)
    {
        String uuid = Json.getString(credentials, "uuid");

        if (uuid != null)
        {
            JSONObject crendital = new JSONObject();

            Json.put(crendital, "uuid", uuid);
            Json.put(crendital, "credentials", credentials);

            IOTCredential newCredential = new IOTCredential(crendital);

            IOTCredentials.addEntry(newCredential, false);
        }
        else
        {
            Log.d(LOGTAG, "registerDeviceCredentials: no device.");
        }
    }

}
