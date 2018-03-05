package zz.top.p2p.camera;

import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.common.Json;

public class P2PCameras
{
    private static final String LOGTAG = P2PCameras.class.getSimpleName();

    public static final Map<String, JSONObject> cameras = new HashMap<>();

    public static void addCamera(JSONObject device)
    {
        String deviceUUID = Json.getString(device, "device_uuid");
        String deviceName = Json.getString(device, "device_name");
        String deviceCategory = Json.getString(device, "device_category");

        Log.d(LOGTAG, "addCamera: name=" + deviceName);

        if ((deviceUUID != null) && (deviceCategory != null) && deviceCategory.equals("p2pcamera"))
        {
            synchronized (cameras)
            {
                cameras.put(deviceUUID, device);
            }
        }
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
    public static String findCameraByNickname(String name)
    {
        for (Map.Entry<String, JSONObject> entry : cameras.entrySet())
        {
            JSONObject camera = entry.getValue();

            String devName = Json.getString(camera, "device_nick");

            if ((devName != null) && devName.equals(name))
            {
                return Json.getString(camera, "device_uuid");
            }
        }

        return null;
    }

    @Nullable
    public static String getP2PDeviceId(String uuid)
    {
        JSONObject camera = cameras.get(uuid);
        return Json.getString(camera, "p2p_id");
    }

    @Nullable
    public static String getP2PDevicePw(String uuid)
    {
        JSONObject camera = cameras.get(uuid);
        return Json.getString(camera, "p2p_pw");
    }
}
