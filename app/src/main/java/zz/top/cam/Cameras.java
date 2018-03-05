package zz.top.cam;

import android.support.annotation.Nullable;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Cameras
{
    private static final String LOGTAG = Cameras.class.getSimpleName();

    public static final Map<String, JSONObject> cameras = new HashMap<>();

    public static void addCamera(JSONObject device)
    {
        String deviceUUID = Json.getString(device, "device_uuid");
        String deviceName = Json.getString(device, "device_name");

        Log.d(LOGTAG, "addCamera:" + " uuid=" + deviceUUID + " name=" + deviceName);

        if (deviceUUID != null)
        {
            synchronized (cameras)
            {
                cameras.put(deviceUUID, device);
            }
        }
    }

    @Nullable
    public static JSONObject getCameraDevice(String deviceUUID)
    {
        return cameras.get(deviceUUID);
    }

    @Nullable
    public static String findCameraByName(String name)
    {
        for (Map.Entry<String, JSONObject> camera : cameras.entrySet())
        {
            JSONObject json = camera.getValue();

            String devName = Json.getString(json, "device_name");

            if ((devName != null) && devName.equals(name))
            {
                return Json.getString(json, "device_uuid");
            }
        }

        return null;
    }

    @Nullable
    public static String findCameraByNick(String nick)
    {
        for (Map.Entry<String, JSONObject> entry : cameras.entrySet())
        {
            JSONObject camera = entry.getValue();

            String devName = Json.getString(camera, "device_nick");

            if ((devName != null) && devName.equals(nick))
            {
                return Json.getString(camera, "device_uuid");
            }
        }

        return null;
    }

    @Nullable
    public static String findCameraByDeviceID(String deviceID)
    {
        for (Map.Entry<String, JSONObject> entry : cameras.entrySet())
        {
            JSONObject camera = entry.getValue();

            String devName = Json.getString(camera, "device_id");

            if ((devName != null) && devName.equals(deviceID))
            {
                return Json.getString(camera, "device_uuid");
            }
        }

        return null;
    }
}
