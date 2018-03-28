package de.xavaro.android.iot.base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTCredentials;
import de.xavaro.android.iot.status.IOTMetadata;
import de.xavaro.android.iot.status.IOTMetadatas;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Log;

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

        Log.d(LOGTAG, "registerDevice: uuid=" + uuid + " name=" + name + " model=" + model);
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
        resolveAndCacheUUID(status);

        String uuid = Json.getString(status, "uuid");
        String ipaddr = Json.getString(status, "ipaddr");

        if (uuid != null)
        {
            Log.d(LOGTAG, "registerDeviceStatus: uuid=" + uuid + " ipaddr=" + ipaddr);

            IOTStatus newStatus = new IOTStatus(status);
            IOTStatus.list.addEntryInternal(newStatus, false);
        }
        else
        {
            Log.d(LOGTAG, "registerDeviceStatus: no device json=" + Json.toPretty(status));
        }
    }

    public void registerDeviceMetadata(JSONObject metadata)
    {
        resolveAndCacheUUID(metadata);

        String uuid = Json.getString(metadata, "uuid");

        if (uuid != null)
        {
            JSONObject internal = new JSONObject();

            Json.put(internal, "uuid", uuid);
            Json.put(internal, "metadata", metadata);

            IOTMetadata newMetatdata = new IOTMetadata(internal);

            IOTMetadatas.addEntry(newMetatdata, false);
        }
        else
        {
            Log.d(LOGTAG, "registerDeviceMetadata: no device json=" + Json.toPretty(metadata));
        }
    }

    public void registerDeviceCredentials(JSONObject credentials)
    {
        resolveAndCacheUUID(credentials);

        String uuid = Json.getString(credentials, "uuid");

        if (uuid != null)
        {
            JSONObject internal = new JSONObject();

            Json.put(internal, "uuid", uuid);
            Json.put(internal, "credentials", credentials);

            IOTCredential newCredential = new IOTCredential(internal);

            IOTCredentials.addEntry(newCredential, false);
        }
        else
        {
            Log.d(LOGTAG, "registerDeviceCredentials: no device json=" + Json.toPretty(credentials));
        }
    }

    private void resolveAndCacheUUID(JSONObject message)
    {
        String uuid = Json.getString(message, "uuid");
        String ipaddr = Json.getString(message, "ipaddr");

        if ((uuid == null) && (ipaddr != null))
        {
            uuid = ipcache.get(ipaddr);

            if (uuid != null) Json.put(message, "uuid", uuid);
        }

        if ((uuid != null) && (ipaddr != null))
        {
            ipcache.put(ipaddr, uuid);
        }
    }
}
