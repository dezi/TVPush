package de.xavaro.android.gui.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.simple.Json;

public class GUISetup
{
    private final static String LOGTAG = GUISetup.class.getSimpleName();

    public static JSONObject getRequiredPermissions()
    {
        JSONObject perms = new JSONObject();

        JSONArray mic = new JSONArray();
        Json.put(mic, Manifest.permission.RECORD_AUDIO);

        JSONArray ble = new JSONArray();
        Json.put(ble, Manifest.permission.BLUETOOTH);
        Json.put(ble, Manifest.permission.BLUETOOTH_ADMIN);

        JSONArray loc = new JSONArray();
        Json.put(loc, Manifest.permission.ACCESS_FINE_LOCATION);
        Json.put(loc, Manifest.permission.ACCESS_COARSE_LOCATION);

        JSONArray ext = new JSONArray();
        Json.put(ext, Manifest.permission.READ_EXTERNAL_STORAGE);
        Json.put(ext, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        JSONArray cam = new JSONArray();
        Json.put(cam, Manifest.permission.CAMERA);
        Json.put(cam, Manifest.permission.CAPTURE_AUDIO_OUTPUT);
        Json.put(cam, Manifest.permission.CAPTURE_VIDEO_OUTPUT);

        Json.put(perms, "mic", mic);
        Json.put(perms, "ble", mic);
        Json.put(perms, "loc", mic);
        Json.put(perms, "ext", mic);
        Json.put(perms, "cam", mic);

        return perms;
    }

    public static boolean checkPermissions(Context context, String area)
    {
        boolean haveRights = false;

        JSONObject perms = getRequiredPermissions();
        JSONArray list = Json.getArray(perms, area);

        if (list != null)
        {
            haveRights = true;

            for (int inx = 0; inx < list.length(); inx++)
            {
                String manifestperm = Json.getString(list, inx);

                haveRights &= havePermission(context, manifestperm);
            }
        }

        return haveRights;
    }

    private static boolean havePermission(Context context, String manifestperm)
    {
        int permission = ContextCompat.checkSelfPermission(context, manifestperm);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }
}
