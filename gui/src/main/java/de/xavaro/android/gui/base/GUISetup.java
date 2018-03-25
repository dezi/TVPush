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
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

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

        Json.put(services, "ble", haveService("ble"));

        //
        // Location.
        //

        Json.put(services, "loc", haveService("loc"));

        //
        // Developer.
        //

        Json.put(services, "dev", haveService("dev"));

        return services;
    }

    public static JSONObject getRequiredPermissions()
    {
        JSONObject perms = new JSONObject();

        //
        // Microphone.
        //

        JSONArray mic = new JSONArray();
        Json.put(mic, Manifest.permission.RECORD_AUDIO);
        Json.put(perms, "mic", mic);

        //
        // Bluetooth.
        //

        JSONArray ble = new JSONArray();
        Json.put(ble, Manifest.permission.BLUETOOTH);
        Json.put(ble, Manifest.permission.BLUETOOTH_ADMIN);
        Json.put(perms, "ble", ble);

        //
        // Location.
        //

        LocationManager locationManager = Simple.getLocationManager();

        boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean netIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

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

        //
        // External storage.
        //

        JSONArray ext = new JSONArray();
        Json.put(ext, Manifest.permission.READ_EXTERNAL_STORAGE);
        Json.put(ext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Json.put(perms, "ext", ext);

        //
        // Camera.
        //

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            if (Simple.isIscamera())
            {
                JSONArray cam = new JSONArray();
                Json.put(cam, Manifest.permission.CAMERA);
                Json.put(perms, "cam", cam);
            }
        }

        return perms;
    }

    public static JSONObject getRequiredFeatures()
    {
        JSONObject features = new JSONObject();

        //
        // USB-Stick.
        //

        if (Simple.isTV())
        {
            Json.put(features, "usb", haveExtStorage());
        }

        //
        // SD-Card.
        //

        if (Simple.isTablet() && ! Simple.isTV())
        {
            Json.put(features, "ssd", haveExtStorage());
        }

        return features;
    }

    public static boolean requestPermission(Activity activity, String area, int requestCode)
    {
        String which = null;

        if (area.equals("mic")) which = Manifest.permission.RECORD_AUDIO;
        if (area.equals("loc")) which = Manifest.permission.ACCESS_FINE_LOCATION;
        if (area.equals("ble")) which = Manifest.permission.BLUETOOTH_ADMIN;
        if (area.equals("ext")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (area.equals("cam")) which = Manifest.permission.CAMERA;

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

    private static boolean haveService(String service)
    {
        boolean enabled = false;

        //
        // Bluetooth.
        //

        if (service.equals("ble"))
        {
            BluetoothAdapter adapter = Simple.getBTAdapter();
            enabled = (adapter != null) && adapter.isEnabled();
        }

        //
        // Location.
        //

        if (service.equals("loc"))
        {
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

            enabled = locgpsEnabled || locnetEnabled;
        }

        //
        // Developer.
        //

        if (service.equals("dev"))
        {
            int devEnabled = Settings.Secure.getInt(Simple.getContentResolver(),
                    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);
        }

        //
        // Services which are always activated.
        //

        if (service.equals("mic") || service.equals("cam") || service.equals("ext"))
        {
            enabled = true;
        }

        return enabled;
    }

    private static boolean havePermission(Context context, String manifestperm)
    {
        int permission = ContextCompat.checkSelfPermission(context, manifestperm);

        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    private static boolean haveExtStorage()
    {
        boolean have = false;

        try
        {
            File storage = new File("/storage");
            File[] mounts = storage.listFiles();

            for (File mount : mounts)
            {
                if (!mount.canRead()) continue;
                if (mount.getName().equals("emulated")) continue;
                if (mount.getName().equals("enc_emulated")) continue;

                have = true;
            }
        }
        catch (Exception ignore)
        {
        }

        Log.d(LOGTAG, "haveExtStorage: have=" + have);

        return have;
    }

    public static int getTextServiceResid()
    {
        return R.string.setup_services_service;
    }

    public static int getTextForServiceResid(String service)
    {
        switch (service)
        {
            case "mic": return R.string.setup_services_service_mic;
            case "ext": return R.string.setup_services_service_ext;
            case "cam": return R.string.setup_services_service_cam;
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
            case "mic": return R.drawable.mic_540;
            case "ext": return R.drawable.read_write_340;
            case "cam": return R.drawable.camera_shutter_820;
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

    public static int getTextFeatureResid()
    {
        return R.string.setup_features_feature;
    }

    public static int getTextForFeatureResid(String service)
    {
        switch (service)
        {
            case "usb": return R.string.setup_features_feature_usb;
            case "ssd": return R.string.setup_features_feature_ssd;
        }

        return R.string.setup_features_feature_ukn;
    }

    public static int getIconForFeatureResid(String service)
    {
        switch (service)
        {
            case "usb": return R.drawable.usb_stick_400;
            case "ssd": return R.drawable.ssd_120;
        }

        return -1;
    }

    public static int getTextForFeatureEnabledResid(String service, boolean enabled)
    {
        if (service.equals("usb"))
        {
            return enabled
                    ? R.string.setup_features_feature_usb_active
                    : R.string.setup_features_feature_usb_inactive;
        }

        if (service.equals("ssd"))
        {
            return enabled
                    ? R.string.setup_features_feature_ssd_active
                    : R.string.setup_features_feature_ssd_inactive;
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
                int devEnabled = Settings.Secure.getInt(Simple.getContentResolver(),
                        Settings.Global.DEVELOPMENT_SETTINGS_ENABLED , 0);

                if (devEnabled == 1)
                {
                    context.startActivity(new Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS));
                }
                else
                {
                    if (Simple.isSony())
                    {
                        //
                        // Fuck dat. Sony engineers fucked it up again.
                        //

                        String pkg = "com.android.tv.settings";
                        String cls = "com.sony.dtv.settings.about.AboutActivity";

                        ComponentName cn = new ComponentName(pkg, cls);
                        Intent intent = new Intent();
                        intent.setComponent(cn);

                        context.startActivity(intent);
                    }
                    else
                    {
                        context.startActivity(new Intent(Settings.ACTION_DEVICE_INFO_SETTINGS));
                    }
                }

                return true;
            }
        }
        catch (Exception ignore)
        {
        }

        return false;
    }

    public static int getTextPermissionResid()
    {
        return R.string.setup_permissions_permission;
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
        }

        return R.string.setup_permissions_perm_unknown;
    }
}
