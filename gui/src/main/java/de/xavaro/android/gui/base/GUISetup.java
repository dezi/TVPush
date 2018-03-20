package de.xavaro.android.gui.base;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.R;

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
        Json.put(perms, "ble", ble);
        Json.put(perms, "loc", loc);
        Json.put(perms, "ext", ext);
        Json.put(perms, "cam", cam);

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

    public static int getTextPermissionResid()
    {
        return R.string.setup_permissions_permission;
    }

    public static int getTextForAreaResid(String area)
    {
        switch (area)
        {
            case "mic": return R.string.setup_permissions_area_mic;
            case "ext": return R.string.setup_permissions_area_ext;
            case "loc": return R.string.setup_permissions_area_loc;
            case "ble": return R.string.setup_permissions_area_ble;
            case "cam": return R.string.setup_permissions_area_cam;
        }

        return R.string.setup_permissions_area_ukn;
    }

    public static int getIconForAreaResid(String area)
    {
        switch (area)
        {
            case "mic": return R.drawable.mic_540;
            case "ext": return R.drawable.bluetooth_450;
            case "loc": return R.drawable.gps_530;
            case "ble": return R.drawable.usb_stick_400;
            case "cam": return R.drawable.camera_shutter_820;
        }

        return -1;
    }

    public static int getTextForPermissionResid(String manifestperm)
    {
        switch (manifestperm)
        {
            case Manifest.permission.RECORD_AUDIO:
                return R.string.setup_permissions_perm_record_audio;
            case Manifest.permission.BLUETOOTH:
                return R.string.setup_permissions_perm_bluetooth;
            case Manifest.permission.BLUETOOTH_ADMIN:
                return R.string.setup_permissions_perm_bluetooth_admin;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return R.string.setup_permissions_perm_access_fine_location;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return R.string.setup_permissions_perm_access_coarse_location;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return R.string.setup_permissions_perm_read_external_storage;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return R.string.setup_permissions_perm_write_external_storage;
            case Manifest.permission.CAMERA:
                return R.string.setup_permissions_perm_camera;
            case Manifest.permission.CAPTURE_AUDIO_OUTPUT:
                return R.string.setup_permissions_perm_capture_audio_output;
            case Manifest.permission.CAPTURE_VIDEO_OUTPUT:
                return R.string.setup_permissions_perm_capture_video_output;
        }

        return R.string.setup_permissions_perm_unknown;
    }
}
