package de.xavaro.android.iot.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.proxim.IOTGeopos;
import de.xavaro.android.iot.comm.IOTMessageHandler;
import de.xavaro.android.iot.proxim.IOTAdvertiser;
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

import de.xavaro.android.pub.interfaces.all.SubSystemHandler;
import de.xavaro.android.pub.interfaces.ext.OnSpeechHandler;
import de.xavaro.android.pub.interfaces.iot.OnADBToolHandlerRequest;
import de.xavaro.android.pub.interfaces.iot.OnStatusRequest;
import de.xavaro.android.pub.interfaces.iot.GetDevices;
import de.xavaro.android.pub.interfaces.pub.PUBADBTool;

public class IOT implements
        SubSystemHandler,
        GetDevices,
        OnStatusRequest,
        OnSpeechHandler,
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

    public IOTAdvertiser proximServer;
    public IOTGeopos proximLocationListener;

    private Application appcontext;

    public IOT(Application appcontext)
    {
        this.appcontext = appcontext;
        Simple.initialize(appcontext);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        IOT.instance = this;
    }

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

        return info;
    }

    @Override
    public JSONObject getSubsystemSettings()
    {
        JSONObject info = getSubsystemInfo();

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
    public void startSubsystem(String subsystem)
    {
        Log.d(LOGTAG, "startSubsystem: subsystem=" + subsystem);

        if (subsystem.equals("iot"))
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

                IOTAdvertiser.startService();

                IOTGeopos.startService(appcontext);

                IOTHandleHelo.sendHELO();

                onSubsystemStarted("iot", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
            }
        }

        if (subsystem.equals("iot") || subsystem.equals("iot.alive"))
        {
            if (getSubsystemState("iot.alive") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
            {
                IOTAlive.startService();

                onSubsystemStarted("iot.alive", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
            }
        }
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        Log.d(LOGTAG, "stopSubsystem: subsystem=" + subsystem);

        if (subsystem.equals("iot") || subsystem.equals("iot.alive"))
        {
            if (getSubsystemState("iot.alive") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
            {
                IOTAlive.stopService();

                onSubsystemStopped("iot.alive", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
            }
        }

        if (subsystem.equals("iot"))
        {
            if (getSubsystemState("iot") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
            {
                IOTGeopos.stopService();

                IOTAdvertiser.stopService();

                IOTHuman.list = null;
                IOTDevice.list = null;
                IOTDomain.list = null;
                IOTLocation.list = null;

                IOTStatus.list = null;
                IOTMetadata.list = null;
                IOTCredential.list = null;

                onSubsystemStopped("iot", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
            }
        }
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "getSubsystemState: STUB! subsystem=" + subsystem);
        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    @Override
    public void setSubsystemState(String subsystem, int state)
    {
        Log.d(LOGTAG, "setSubsystemState: STUB!");
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
    public JSONObject getStatus(String uuid)
    {
        IOTStatus status = IOTStatus.list.getEntry(uuid);
        return (status != null) ? status.toJson() : null;
    }

    @Override
    public JSONObject getCredential(String uuid)
    {
        IOTCredential credential = IOTCredential.list.getEntry(uuid);
        return (credential != null) ? credential.toJson() : null;
    }

    @Override
    public JSONObject getMetadata(String uuid)
    {
        IOTMetadata metadata = IOTMetadata.list.getEntry(uuid);
        return (metadata != null) ? metadata.toJson() : null;
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

    //region OnSpeechHandler

    @Override
    public void onActivateRemote()
    {
        Log.d(LOGTAG, "onActivateRemote: STUB!");
    }

    @Override
    public void onSpeechReady()
    {
        Log.d(LOGTAG, "onSpeechReady: STUB!");
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: STUB!");
    }

    //endregion OnSpeechHandler

    @Override
    public boolean onDeviceStatusRequest(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceStatusRequest: STUB!");

        return false;
    }
}
