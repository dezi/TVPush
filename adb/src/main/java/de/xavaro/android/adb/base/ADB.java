package de.xavaro.android.adb.base;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;

import de.xavaro.android.adb.R;
import de.xavaro.android.adb.simple.Json;
import de.xavaro.android.adb.simple.Log;
import de.xavaro.android.adb.simple.Simple;
import pub.android.interfaces.iot.OnDeviceRequest;

public class ADB implements
        SubSystemHandler,
        OnDeviceRequest
{
    private static final String LOGTAG = ADB.class.getSimpleName();

    public static ADB instance;

    public ADB(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "adb");

        Json.put(info, "name", Simple.getTrans(R.string.subsystem_adb_name));
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_MANDATORY);
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_adb_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_adb_280));

        JSONArray adbDevices = onDeviceCapabilityRequest("adb");

        if ((adbDevices != null) && (adbDevices.length() > 0))
        {
            JSONArray settings = new JSONArray();
            Json.put(info, "settings", settings);

            for (int inx = 0; inx < adbDevices.length(); inx++)
            {
                String uuid = Json.getString(adbDevices, inx);

                JSONObject adbDevice = onDeviceRequest(uuid);
                if (adbDevice == null) continue;

                String name = Json.getString(adbDevice, "name");

                JSONObject setting = new JSONObject();

                Json.put(setting, "tag",  uuid);
                Json.put(setting, "name", name);
                Json.put(setting, "type", SubSystemHandler.SUBSYSTEM_TYPE_FEATURE);
                Json.put(setting, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
                Json.put(setting, "info", "nix");
                Json.put(setting, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_adb_280));

                Json.put(settings, setting);
            }
        }

        Log.d(LOGTAG, "getSubsystemInfo: json=" + Json.toPretty(info));

        return info;
    }

    @Override
    public void startSubsystem()
    {
        if (onGetSubsystemState("adb") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            onSubsystemStarted("adb", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
        }
    }

    @Override
    public void stopSubsystem()
    {
        if (onGetSubsystemState("adb") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
        {
            onSubsystemStopped("adb", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
        }
    }

    @Override
    public int onGetSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "onGetSubsystemState: STUB!");

        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    @Override
    public void onSubsystemStarted(String subsystem, int runstate)
    {
        Log.d(LOGTAG, "onSubsystemStarted: STUB!");
    }

    @Override
    public void onSubsystemStopped(String subsystem, int runstate)
    {
        Log.d(LOGTAG, "onSubsystemStopped: STUB!");
    }

    @Override
    public JSONObject onDeviceRequest(String uuid)
    {
        Log.d(LOGTAG, "onDeviceRequest: STUB!");

        return null;
    }

    @Override
    public JSONArray onDeviceCapabilityRequest(String capability)
    {
        Log.d(LOGTAG, "onDeviceCapabilityRequest: STUB!");

        return null;
    }
}