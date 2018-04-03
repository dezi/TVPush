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

import java.io.File;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.R;
import pub.android.interfaces.all.SubSystemHandler;

public class GUISetup
{
    private final static String LOGTAG = GUISetup.class.getSimpleName();

    public static int getIconForNeedResid(String need)
    {
        switch (need)
        {
            case "mic": return R.drawable.mic_540;
            case "ext": return R.drawable.read_write_340;
            case "cam": return R.drawable.camera_shutter_820;
            case "ble": return R.drawable.bluetooth_450;
            case "loc": return R.drawable.gps_530;
            case "dev": return R.drawable.developer_512;
            case "usb": return R.drawable.usb_stick_400;
            case "ssd": return R.drawable.ssd_120;
            case "adb": return R.drawable.adb_220;
            case "pin": return R.drawable.setting_pincode_270;
        }

        return R.drawable.unknown_550;
    }

    public static int getTextForNeedResid(String need)
    {
        switch (need)
        {
            case "mic": return R.string.setup_need_head_mic;
            case "ext": return R.string.setup_need_head_ext;
            case "cam": return R.string.setup_need_head_cam;
            case "ble": return R.string.setup_need_head_ble;
            case "loc": return R.string.setup_need_head_loc;
            case "dev": return R.string.setup_need_head_dev;
            case "usb": return R.string.setup_need_head_usb;
            case "ssd": return R.string.setup_need_head_ssd;
            case "adb": return R.string.setup_need_head_adb;
            case "pin": return R.string.setup_need_head_pin;
        }

        return R.string.setup_ukn;
    }

    public static int getInfoForNeedResid(String need)
    {
        switch (need)
        {
            case "mic": return R.string.setup_need_info_mic;
            case "ext": return R.string.setup_need_info_ext;
            case "cam": return R.string.setup_need_info_cam;
            case "ble": return R.string.setup_need_info_ble;
            case "loc": return R.string.setup_need_info_loc;
            case "dev": return R.string.setup_need_info_dev;
            case "usb": return R.string.setup_need_info_usb;
            case "ssd": return R.string.setup_need_info_ssd;
            case "adb": return R.string.setup_need_info_adb;
            case "pin": return R.string.setup_need_info_pin;
        }

        return R.string.setup_ukn;
    }

    public static int getTextForNeedStatusResid(String need, boolean enabled)
    {
        if (need.equals("usb"))
        {
            return enabled
                    ? R.string.setup_need_status_usb_active
                    : R.string.setup_need_status_usb_inactive;
        }

        if (need.equals("ssd"))
        {
            return enabled
                    ? R.string.setup_need_status_ssd_active
                    : R.string.setup_need_status_ssd_inactive;
        }

        if (need.equals("adb"))
        {
            return enabled
                    ? R.string.setup_need_status_adb_active
                    : R.string.setup_need_status_adb_inactive;
        }

        if (need.equals("pin"))
        {
            return enabled
                    ? R.string.setup_need_status_pin_active
                    : R.string.setup_need_status_pin_inactive;
        }

        return enabled
                ? R.string.setup_need_status_active
                : R.string.setup_need_status_inactive;
    }

    public static int getIconForPermResid(String perm)
    {
        switch (perm)
        {
            case "mic": return R.drawable.mic_540;
            case "cam": return R.drawable.camera_shutter_820;
            case "ble": return R.drawable.bluetooth_450;
            case "loc": return R.drawable.position_560;
            case "ext": return R.drawable.read_write_340;
            case "usb": return R.drawable.read_write_340;
            case "ssd": return R.drawable.read_write_340;
        }

        return R.drawable.unknown_550;
    }

    public static int getTextForPermResid(String need)
    {
        switch (need)
        {
            case "mic": return R.string.setup_perm_head_mic;
            case "cam": return R.string.setup_perm_head_cam;
            case "ble": return R.string.setup_perm_head_ble;
            case "loc": return R.string.setup_perm_head_loc;
            case "ext": return R.string.setup_perm_head_ext;
            case "usb": return R.string.setup_perm_head_ext;
            case "ssd": return R.string.setup_perm_head_ext;
        }

        return R.string.setup_ukn;
    }

    public static int getInfoForPermResid(String need)
    {
        switch (need)
        {
            case "mic": return R.string.setup_perm_info_mic;
            case "cam": return R.string.setup_perm_info_cam;
            case "ble": return R.string.setup_perm_info_ble;
            case "loc": return R.string.setup_perm_info_loc;
            case "ext": return R.string.setup_perm_info_ext;
            case "usb": return R.string.setup_perm_info_ext;
            case "ssd": return R.string.setup_perm_info_ext;
        }

        return R.string.setup_ukn;
    }

    public static int getTextForManifestPermResid(String manifestperm)
    {
        switch (manifestperm)
        {
            case Manifest.permission.RECORD_AUDIO:
                return R.string.setup_manifest_perm_record_audio;
            case Manifest.permission.BLUETOOTH:
                return R.string.setup_manifest_perm_bluetooth;
            case Manifest.permission.BLUETOOTH_ADMIN:
                return R.string.setup_manifest_perm_bluetooth_admin;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return R.string.setup_manifest_perm_access_fine_location;
            case Manifest.permission.ACCESS_COARSE_LOCATION:
                return R.string.setup_manifest_perm_access_coarse_location;
            case Manifest.permission.READ_EXTERNAL_STORAGE:
                return R.string.setup_manifest_perm_read_external_storage;
            case Manifest.permission.WRITE_EXTERNAL_STORAGE:
                return R.string.setup_manifest_perm_write_external_storage;
            case Manifest.permission.CAMERA:
                return R.string.setup_manifest_perm_camera;
        }

        return R.string.setup_ukn;
    }

    public static boolean haveNeed(String need)
    {
        boolean have = false;

        //
        // Bluetooth.
        //

        if (need.equals("ble"))
        {
            BluetoothAdapter adapter = Simple.getBTAdapter();
            have = (adapter != null) && adapter.isEnabled();
        }

        //
        // Location.
        //

        if (need.equals("loc"))
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

            have = locgpsEnabled || locnetEnabled;
        }

        //
        // Developer.
        //

        if (need.equals("dev"))
        {
            int devEnabled = Settings.Global.getInt(Simple.getContentResolver(),
                    Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

            have = (devEnabled == 1);
        }

        //
        // Services which are always activated.
        //

        if (need.equals("mic") || need.equals("cam") || need.equals("ext"))
        {
            have = true;
        }

        //
        // Special needs.
        //

        if (need.equals("usb") || need.equals("ssd"))
        {
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
        }

        if (need.equals("adb"))
        {
            // Todo: get adb check.
        }

        return have;
    }

    public static boolean needHasService(String need)
    {
        return need.equals("ble") || need.equals("loc") || need.equals("dev");
    }

    public static boolean needHasInfos(String need)
    {
        return need.equals("usb") || need.equals("ssd");
    }

    public static boolean needHasAuth(String need)
    {
        return need.equals("adb") || need.equals("pin");
    }

    public static boolean needHasPermissions(String need)
    {
        return (getPermissionsForNeed(need).length() > 0);
    }

    public static JSONArray getPermissionsForNeed(String need)
    {
        JSONArray perms = new JSONArray();

        //
        // Microphone.
        //

        if (need.equals("mic"))
        {
            Json.put(perms, Manifest.permission.RECORD_AUDIO);
        }

        //
        // Bluetooth.
        //

        if (need.equals("ble"))
        {
            Json.put(perms, Manifest.permission.BLUETOOTH);
            Json.put(perms, Manifest.permission.BLUETOOTH_ADMIN);
        }

        //
        // Location.
        //

        if (need.equals("loc"))
        {
            LocationManager locationManager = Simple.getLocationManager();

            boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            boolean netIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (netIsEnabled || gpsIsEnabled)
            {
                if (netIsEnabled)
                {
                    Json.put(perms, Manifest.permission.ACCESS_COARSE_LOCATION);
                }

                if (gpsIsEnabled)
                {
                    Json.put(perms, Manifest.permission.ACCESS_FINE_LOCATION);
                }
            }
            else
            {
                //
                // None enabled. Put both into
                // required permissions for now.
                //

                Json.put(perms, Manifest.permission.ACCESS_COARSE_LOCATION);
                Json.put(perms, Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }

        //
        // External storage.
        //

        if (need.equals("ext") || need.equals("usb") || need.equals("ssd"))
        {
            Json.put(perms, Manifest.permission.READ_EXTERNAL_STORAGE);
            Json.put(perms, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        //
        // Camera.
        //

        if (need.equals("cam"))
        {
            Json.put(perms, Manifest.permission.CAMERA);
        }

        return perms;
    }

    public static void startIntentForNeed(Context context, String service)
    {
        try
        {
            if (service.equals("loc"))
            {
                context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
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
            }

            if (service.equals("dev"))
            {
                if (haveNeed("dev"))
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
            }
        }
        catch (Exception ignore)
        {
        }
    }

    public static void requestPermissionForNeed(Activity activity, String need, int requestCode)
    {
        String which = null;

        if (need.equals("mic")) which = Manifest.permission.RECORD_AUDIO;
        if (need.equals("loc")) which = Manifest.permission.ACCESS_FINE_LOCATION;
        if (need.equals("ble")) which = Manifest.permission.BLUETOOTH_ADMIN;
        if (need.equals("cam")) which = Manifest.permission.CAMERA;

        if (need.equals("ext")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (need.equals("usb")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (need.equals("ssd")) which = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if ((which != null) && ! havePermission(activity, which))
        {
            ActivityCompat.requestPermissions(activity, new String[]{which}, requestCode);

            return;
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
    }

    public static boolean havePermission(Context context, String manifestperm)
    {
        int permission = ContextCompat.checkSelfPermission(context, manifestperm);

        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    public static boolean haveAllPermissionsForNeed(Context context, String need)
    {
        boolean haveAll = false;

        JSONArray list = getPermissionsForNeed(need);

        if (list != null)
        {
            haveAll = true;

            for (int inx = 0; inx < list.length(); inx++)
            {
                String manifestperm = Json.getString(list, inx);

                haveAll &= havePermission(context, manifestperm);
            }
        }

        return haveAll;
    }

    //region Subsystems.

    public static JSONArray getAvailableSubsystems()
    {
        return GUI.instance.subSystems.getRegisteredSubsystems();
    }

    public static int getSubsystemState(String subsystem)
    {
        return GUI.instance.subSystems.getSubsystemState(subsystem);
    }

    public static int getSubsystemRunState(String subsystem)
    {
        return GUI.instance.subSystems.getSubsystemRunState(subsystem);
    }

    public static String getTextForSubsystemEnabled(String subsystem, int state)
    {
        if (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            return Simple.getTrans(R.string.setup_state_active, subsystem);
        }
        else
        {
            return Simple.getTrans(R.string.setup_state_inactive, subsystem);
        }
    }

    public static String getTextForSubsystemEnabled(String subsystem, int state, int mode)
    {
        if (mode == SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE)
        {
            return Simple.getTrans(R.string.setup_state_impossible, subsystem);
        }

        if (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            return Simple.getTrans(R.string.setup_state_active, subsystem);
        }
        else
        {
            return Simple.getTrans(R.string.setup_state_inactive, subsystem);
        }
    }

    public static int getTextForSubsystemRunstateResid(int runstate)
    {
        switch (runstate)
        {
            case SubSystemHandler.SUBSYSTEM_RUN_STARTED: return R.string.setup_runstates_started;
            case SubSystemHandler.SUBSYSTEM_RUN_STOPPED: return R.string.setup_runstates_stopped;
            case SubSystemHandler.SUBSYSTEM_RUN_FAILED: return R.string.setup_runstates_failed;
            case SubSystemHandler.SUBSYSTEM_RUN_ZOMBIE: return R.string.setup_runstates_zombie;
        }

        return R.string.setup_ukn;
    }

    //endregion Subsystems.
}
