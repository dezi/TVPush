package de.xavaro.android.p2pcamera;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.common.Json;

public class P2PCameras
{
    public static final Map<String, JSONObject> cameras = new HashMap<>();

    public static void addCamera(JSONObject device)
    {
        String deviceUUID = Json.getString(device, "device_uuid");
        String deviceCategory = Json.getString(device, "device_category");

        if ((deviceUUID != null) && (deviceCategory != null) && deviceCategory.equals("p2pcamera"))
        {
            synchronized (cameras)
            {
                cameras.put(deviceUUID, device);
            }
        }
    }
}
