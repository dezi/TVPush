package zz.top.cam;

import android.support.annotation.Nullable;

import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import pub.android.interfaces.cam.Camera;
import zz.top.p2p.camera.P2PCamera;
import zz.top.utl.Json;

public class Cameras
{
    private static final String LOGTAG = Cameras.class.getSimpleName();

    public static final Map<String, JSONObject> cameras = new HashMap<>();

    public static void addCamera(JSONObject camera)
    {
        JSONObject device = Json.getObject(camera, "device");

        String deviceUUID = Json.getString(device, "uuid");
        String deviceName = Json.getString(device, "name");

        Log.d(LOGTAG, "addCamera:" + " uuid=" + deviceUUID + " name=" + deviceName);

        if (deviceUUID != null)
        {
            synchronized (cameras)
            {
                cameras.put(deviceUUID, camera);
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
        for (Map.Entry<String, JSONObject> entry : cameras.entrySet())
        {
            JSONObject camera = entry.getValue();

            JSONObject device = Json.getObject(camera, "device");

            String deviceUUID = Json.getString(device, "uuid");
            String deviceName = Json.getString(device, "name");

            if ((deviceUUID != null) && (deviceName != null) && deviceName.equals(name))
            {
                return deviceUUID;
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

            JSONObject device = Json.getObject(camera, "device");

            String deviceUUID = Json.getString(device, "uuid");
            String deviceNick = Json.getString(device, "nick");

            if ((deviceUUID != null) && (deviceNick != null) && deviceNick.equals(nick))
            {
                return deviceUUID;
            }
        }

        return null;
    }

    @Nullable
    public static String findCameraByDeviceID(String id)
    {
        for (Map.Entry<String, JSONObject> entry : cameras.entrySet())
        {
            JSONObject camera = entry.getValue();

            JSONObject device = Json.getObject(camera, "device");

            String deviceUUID = Json.getString(device, "uuid");
            String deviceID = Json.getString(device, "id");

            if ((deviceUUID != null) && (deviceID != null) && deviceID.equals(id))
            {
                return deviceUUID;
            }
        }

        return null;
    }
    @Nullable
    public static Camera createCameraByName(String name)
    {
        return createCameraByUUID(Cameras.findCameraByName(name));
    }

    @Nullable
    public static Camera createCameraByNick(String nick)
    {
        return createCameraByUUID(Cameras.findCameraByNick(nick));
    }

    @Nullable
    public static Camera createCameraByDeviceID(String deviceID)
    {
        return createCameraByUUID(Cameras.findCameraByDeviceID(deviceID));
    }

    @Nullable
    public static Camera createCameraByUUID(String uuid)
    {
        JSONObject camera = Cameras.getCameraDevice(uuid);

        JSONObject device = Json.getObject(camera, "device");

        String deviceUUID = Json.getString(device, "uuid");
        String deviceName = Json.getString(device, "name");
        String deviceDriver = Json.getString(device, "driver");

        Log.d(LOGTAG, "createCameraByUUID:"
                + " uuid=" + deviceUUID
                + " name=" + deviceName
                + " driver=" + deviceDriver
                );

        Camera newcamera = null;

        if (deviceDriver != null)
        {
            if (deviceDriver.equals("yi-p2p"))
            {
                Log.d(LOGTAG, "createCameraByUUID: found=" + deviceDriver);

                newcamera = new P2PCamera();
            }
        }

        if (newcamera != null)
        {
            newcamera.attachCamera(uuid);
        }

        return newcamera;
    }
}
