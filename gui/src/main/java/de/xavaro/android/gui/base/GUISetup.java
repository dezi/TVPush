package de.xavaro.android.gui.base;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.R;

public class GUISetup
{
    private final static String LOGTAG = GUISetup.class.getSimpleName();

    public static JSONObject getRequiredServices()
    {
        JSONObject services = new JSONObject();

        //
        // Bluetooth.
        //

        BluetoothAdapter adapter = Simple.getBTAdapter();
        boolean btAdapter = (adapter != null);
        boolean btAdapterEnabled = btAdapter && adapter.isEnabled();

        Json.put(services, "ble", btAdapterEnabled);

        //
        // Location.
        //

        LocationManager locationManager = Simple.getLocationManager();
        boolean locmanEnabled = (locationManager != null);
        boolean locgpsEnabled = false;
        boolean locnetEnabled = false;

        if (locmanEnabled)
        {
            try
            {
                locgpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            }
            catch (Exception ignore)
            {
            }

            try
            {
                locnetEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            }
            catch (Exception ignore)
            {
            }
        }

        Json.put(services, "loc", locgpsEnabled | locnetEnabled);

        //
        // Developer and ADB.
        //

        int devEnabled = Settings.Secure.getInt(Simple.getContentResolver(),
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0);


        Json.put(services, "dev", locgpsEnabled | locnetEnabled);

        return services;
    }

    public static JSONObject getRequiredPermissions()
    {
        LocationManager locationManager = Simple.getLocationManager();

        boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean netIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        JSONObject perms = new JSONObject();

        JSONArray mic = new JSONArray();
        Json.put(mic, Manifest.permission.RECORD_AUDIO);
        Json.put(perms, "mic", mic);

        JSONArray ble = new JSONArray();
        Json.put(ble, Manifest.permission.BLUETOOTH);
        Json.put(ble, Manifest.permission.BLUETOOTH_ADMIN);
        Json.put(perms, "ble", ble);

        if (netIsEnabled || gpsIsEnabled)
        {
            JSONArray loc = new JSONArray();

            if (netIsEnabled)
            {
                Json.put(loc, Manifest.permission.ACCESS_COARSE_LOCATION);
            }

            if (gpsIsEnabled)
            {
                Json.put(loc, Manifest.permission.ACCESS_FINE_LOCATION);
            }

            Json.put(perms, "loc", loc);
        }

        JSONArray ext = new JSONArray();
        Json.put(ext, Manifest.permission.READ_EXTERNAL_STORAGE);
        Json.put(ext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Json.put(perms, "ext", ext);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            if (Simple.isIscamera())
            {
                JSONArray cam = new JSONArray();
                Json.put(cam, Manifest.permission.CAMERA);
                Json.put(cam, Manifest.permission.CAPTURE_AUDIO_OUTPUT);
                Json.put(cam, Manifest.permission.CAPTURE_VIDEO_OUTPUT);
                Json.put(perms, "cam", cam);
            }
        }

        return perms;
    }

    public static boolean requestPermission(Activity activity, String area, int requestCode)
    {
        String which = null;

        if (area.equals("mic")) which = Manifest.permission.RECORD_AUDIO;
        if (area.equals("loc")) which = Manifest.permission.ACCESS_FINE_LOCATION;
        if (area.equals("ble")) which = Manifest.permission.BLUETOOTH_ADMIN;
        if (area.equals("ext")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (area.equals("cam")) which = Manifest.permission.CAPTURE_VIDEO_OUTPUT;

        if ((which != null) && ! havePermission(activity, which))
        {
            ActivityCompat.requestPermissions(activity, new String[]{which}, requestCode);

            return true;
        }

        //
        // Open the complete permissions
        // setup page for current app.
        //

        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(uri);
        activity.startActivity(intent);

        return false;
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

    public static int getTextServiceResid()
    {
        return R.string.setup_services_service;
    }

    public static int getTextForServiceResid(String service)
    {
        switch (service)
        {
            case "ble": return R.string.setup_services_service_ble;
            case "loc": return R.string.setup_services_service_loc;
            case "dev": return R.string.setup_services_service_dev;
        }

        return R.string.setup_services_service_ukn;
    }

    public static int getIconForServiceResid(String service)
    {
        switch (service)
        {
            case "ble": return R.drawable.bluetooth_450;
            case "loc": return R.drawable.gps_530;
            case "dev": return R.drawable.developer_512;
        }

        return -1;
    }

    public static int getTextForServiceEnabledResid(String service, boolean enabled)
    {
        if (service.equals("ble"))
        {
            return enabled
                    ? R.string.setup_services_service_ble_active
                    : R.string.setup_services_service_ble_inactive;
        }

        if (service.equals("loc"))
        {
            return enabled
                    ? R.string.setup_services_service_loc_active
                    : R.string.setup_services_service_loc_inactive;
        }

        if (service.equals("dev"))
        {
            return enabled
                    ? R.string.setup_services_service_dev_active
                    : R.string.setup_services_service_dev_inactive;
        }

        return -1;
    }

    public static boolean startIntentForService(Context context, String service)
    {
        try
        {
            if (service.equals("loc"))
            {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return true;
            }

            if (service.equals("ble"))
            {
                if (Simple.isSony())
                {
                    //
                    // Fuck dat. Sony engineers fucked it up.
                    //

                    String pkg = "com.android.tv.settings";
                    String cls = "com.sony.dtv.settings.networkaccessories.bluetooth.BluetoothActivity";

                    ComponentName cn = new ComponentName(pkg, cls);
                    Intent intent = new Intent();
                    intent.setComponent(cn);

                    context.startActivity(intent);
                }
                else
                {
                    context.startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                }

                return true;
            }

            if (service.equals("dev"))
            {
                context.startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                return true;
            }

        }
        catch (Exception ex)
        {
        }

        return false;
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
            case "ext": return Simple.isSony() ? R.drawable.usb_stick_400 : R.drawable.ssd_120;
            case "loc": return R.drawable.gps_530;
            case "ble": return R.drawable.bluetooth_450;
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
