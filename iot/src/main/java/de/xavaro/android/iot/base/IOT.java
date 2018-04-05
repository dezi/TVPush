package de.xavaro.android.iot.base;

import android.app.Application;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.proxim.IOTProximLocation;
import de.xavaro.android.iot.proxim.IOTProximScanner;
import de.xavaro.android.iot.comm.IOTMessageHandler;
import de.xavaro.android.iot.proxim.IOTProximServer;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTMetadata;
import de.xavaro.android.iot.things.IOTLocation;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.handler.IOTHandleHelo;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.R;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.iot.OnADBToolHandlerRequest;
import pub.android.interfaces.iot.OnStatusRequest;
import pub.android.interfaces.iot.GetDevices;
import pub.android.interfaces.pub.PUBADBTool;

public class IOT implements
        SubSystemHandler,
        GetDevices,
        OnStatusRequest,
        OnADBToolHandlerRequest
{
    private static final String LOGTAG = IOT.class.getSimpleName();

    public static IOT instance;

    public static IOTBoot boot;
    public static IOTHuman human;
    public static IOTDevice device;
    public static IOTDomain domain;

    public static IOTMessageHandler message;

    public IOTAlive alive;
    public IOTRegister register;

    public IOTProximServer proximServer;
    public IOTProximScanner proximScanner;
    public IOTProximLocation proximLocationListener;

    private Application appcontext;

    public IOT(Application appcontext)
    {
        this.appcontext = appcontext;
        Simple.initialize(appcontext);
    }

    //region SubSystemHandler

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "iot");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_MANDATORY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_iot_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_iot_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_iot_200));
        Json.put(info, "need", "loc");

        JSONArray settings = new JSONArray();
        Json.put(info, "settings", settings);

        JSONObject alive = new JSONObject();

        Json.put(alive, "tag", "alive");
        Json.put(alive, "name", Simple.getTrans(R.string.subsystem_iot_alive_name));
        Json.put(alive, "type", SubSystemHandler.SUBSYSTEM_TYPE_SERVICE);
        Json.put(alive, "mode", SubSystemHandler.SUBSYSTEM_MODE_DEFAULTACT);
        Json.put(alive, "info", Simple.getTrans(R.string.subsystem_iot_alive_info));
        Json.put(alive, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_iot_alive_440));

        Json.put(settings, alive);

        return info;
    }

    @Override
    public void startSubsystem()
    {
        if (getSubsystemState("iot") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            IOTHuman.list = new IOTList<>((new IOTHuman()).getClassKey());
            IOTDevice.list = new IOTList<>((new IOTDevice()).getClassKey());
            IOTDomain.list = new IOTList<>((new IOTDomain()).getClassKey());
            IOTLocation.list = new IOTList<>((new IOTLocation()).getClassKey());

            IOTStatus.list = new IOTList<>((new IOTStatus()).getClassKey());
            IOTMetadata.list = new IOTList<>((new IOTMetadata()).getClassKey());
            IOTCredential.list = new IOTList<>((new IOTCredential()).getClassKey());

            register = new IOTRegister();

            IOTBoot.initialize();

            IOTService.startService(appcontext);

            IOTMessageHandler.initialize();

            IOTProximServer.startService();

            IOTProximScanner.startService(appcontext);

            IOTProximLocation.startService(appcontext);

            IOTHandleHelo.sendHELO();

            onSubsystemStarted("iot", SubSystemHandler.SUBSYSTEM_RUN_STARTED);

            if (getSubsystemState("iot.alive") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
            {
                IOTAlive.startService();

                onSubsystemStarted("iot.alive", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
            }
        }
    }

    @Override
    public void stopSubsystem()
    {
        if (getSubsystemState("iot") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
        {
            IOTAlive.stopService();

            IOTProximLocation.stopService();

            IOTProximServer.stopService();

            IOTHuman.list = null;
            IOTDevice.list = null;
            IOTDomain.list = null;
            IOTLocation.list = null;

            IOTStatus.list = null;
            IOTMetadata.list = null;
            IOTCredential.list = null;

            onSubsystemStopped("iot", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
        }

        if (getSubsystemState("iot.alive") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
        {
            IOTAlive.stopService();

            onSubsystemStopped("iot.alive", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
        }
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "getSubsystemState: STUB! subsystem=" + subsystem);
        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStarted: STUB! state=" + state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStopped: STUB! state=" + state);
    }

    //endregion SubSystemHandler

    //region GetDevices

    @Override
    public JSONObject getDevice(String uuid)
    {
        IOTDevice device = IOTDevice.list.getEntry(uuid);
        return (device != null) ? device.toJson() : null;
    }

    @Override
    public JSONArray getDevicesWithCapability(String capability)
    {
        JSONArray result = new JSONArray();

        JSONArray list = IOTDevice.list.getUUIDList();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            if (uuid == null) continue;

            IOTDevice device = IOTDevice.list.getEntry(uuid);
            if (device == null) continue;

            if (device.hasCapability(capability))
            {
                Json.put(result, uuid);
            }
        }

        return result;
    }

    //endregion GetDevices

    //region OnADBToolHandlerRequest

    @Override
    public PUBADBTool onADBToolHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "onADBToolHandlerRequest: STUB!");

        return null;
    }

    //endregion OnADBToolHandlerRequest

    @Override
    public boolean onDeviceStatusRequest(JSONObject iotDevice)
    {
        Log.d(LOGTAG, "onDeviceStatusRequest: STUB!");

        return false;
    }
}
