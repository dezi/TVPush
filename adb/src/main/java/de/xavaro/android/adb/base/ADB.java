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

    private final Application appcontext;

    public ADB(Application application)
    {
        appcontext = application;

        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        ADB.instance = this;
    }

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

        return info;
    }

    @Override
    public JSONObject getSubsystemSettings()
    {
        JSONObject info = getSubsystemInfo();

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
    public void startSubsystem(String subsystem)
    {
        String[] parts = subsystem.split("\\.");
        String drv = parts[0];

        if (parts.length == 1)
        {
            //
            // Activate service as such.
            //

            if (getSubsystemState(drv) == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
            {
                //
                // Not much to be done.
                //

                onSubsystemStarted(drv, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
            }
        }
        else
        {
            //
            // Check for subservices. Means target devices. Starting
            // means check, if they are configured and ready to use.
            //

            String uuid = parts[1];

            JSONObject status = onGetStatusRequest(uuid);
            String ipaddr = Json.getString(status, "ipaddr");

            Log.d(LOGTAG, "startSubsystem: uuid=" + uuid + " ipaddr=" + ipaddr);

            if (ipaddr != null)
            {
                ADBToolHandler adbtool = new ADBToolHandler(ipaddr);
                boolean configured = adbtool.isConfigured();
                boolean authorized = adbtool.isAuthorized(appcontext);

                Log.d(LOGTAG, "startSubsystem:"
                        + " subsystem=" + subsystem
                        + " ipaddr=" + ipaddr
                        + " configured=" + configured
                        + " authorized=" + authorized);

                if (authorized)
                {
                    setSubsystemState(subsystem, SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);
                }
                else
                {
                    setSubsystemState(subsystem, SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED);
                }
            }
        }
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        String[] parts = subsystem.split("\\.");
        String drv = parts[0];

        if (parts.length == 1)
        {
            //
            // Deactivate service as such.
            //

            if (getSubsystemState(drv) == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
            {
                //
                // Not much to be done.
                //

                onSubsystemStopped(drv, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
            }
        }
        else
        {
            //
            // Check for subservices. Means target devices.
            // Stopping simply set state to deactivated.
            //

            setSubsystemState(subsystem, SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED);
        }
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "getSubsystemState: STUB!");

        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    @Override
    public void setSubsystemState(String subsystem, int state)
    {
        Log.d(LOGTAG, "setSubsystemState: STUB!");
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
    public JSONObject onGetStatusRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetStatusRequest: STUB!");

        return null;
    }

    @Override
    public JSONObject onGetCredentialRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetCredentialRequest: STUB!");

        return null;
    }

    @Override
    public JSONObject onGetMetaRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetMetaRequest: STUB!");

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