package de.xavaro.android.adb.base;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.adb.GetADBToolHandler;
import pub.android.interfaces.ext.GetDevicesRequest;
import pub.android.interfaces.pub.PUBADBTool;

import de.xavaro.android.adb.publics.ADBToolHandler;

import de.xavaro.android.adb.simple.Simple;
import de.xavaro.android.adb.simple.Json;
import de.xavaro.android.adb.simple.Log;
import de.xavaro.android.adb.R;

public class ADB implements
        SubSystemHandler,
        GetADBToolHandler,
        GetDevicesRequest
{
    private static final String LOGTAG = ADB.class.getSimpleName();

    public static ADB instance;

    public ADB(Application application)
    {
        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "adb");

        Json.put(info, "name", Simple.getTrans(R.string.subsystem_adb_name));
        Json.put(info, "type", SubSystemHandler.SUBSYSTEM_TYPE_FEATURE);
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_MANDATORY);
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_adb_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_adb_280));

        JSONArray adbDevices = onGetDevicesCapabilityRequest("adb");

        if ((adbDevices != null) && (adbDevices.length() > 0))
        {
            JSONArray settings = new JSONArray();
            Json.put(info, "settings", settings);

            for (int inx = 0; inx < adbDevices.length(); inx++)
            {
                String uuid = Json.getString(adbDevices, inx);

                JSONObject adbDevice = onGetDeviceRequest(uuid);
                if (adbDevice == null) continue;

                String name = Json.getString(adbDevice, "name");

                JSONObject setting = new JSONObject();

                Json.put(setting, "uuid",  uuid);
                Json.put(setting, "name", name);
                Json.put(setting, "type", SubSystemHandler.SUBSYSTEM_TYPE_FEATURE);
                Json.put(setting, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
                Json.put(setting, "info", Simple.getTrans(R.string.subsystem_adb_auth));

                Json.put(settings, setting);
            }
        }

        return info;
    }

    @Override
    public void startSubsystem()
    {
        if (getSubsystemState("adb") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            onSubsystemStarted("adb", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
        }
    }

    @Override
    public void stopSubsystem()
    {
        if (getSubsystemState("adb") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
        {
            onSubsystemStopped("adb", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
        }
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "getSubsystemState: STUB!");

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

    //endregion SubSystemHandler

    //region GetADBToolHandler

    @Override
    public PUBADBTool getADBToolHandler(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String ipaddr = Json.getString(status, "ipaddr");

        return (ipaddr != null) ? new ADBToolHandler(ipaddr) : null;
    }

    //endregion GetADBToolHandler

    //region GetDevicesRequest

    @Override
    public JSONObject onGetDeviceRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetDeviceRequest: STUB!");

        return null;
    }

    @Override
    public JSONArray onGetDevicesCapabilityRequest(String capability)
    {
        Log.d(LOGTAG, "onGetDevicesCapabilityRequest: STUB!");

        return null;
    }

    //endregion GetDevicesRequest
}