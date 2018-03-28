package de.xavaro.android.iot.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.comm.IOTMessageHandler;
import de.xavaro.android.iot.handler.IOTHandleHelo;
import de.xavaro.android.iot.proxim.IOTProximLocation;
import de.xavaro.android.iot.proxim.IOTProximScanner;
import de.xavaro.android.iot.proxim.IOTProximServer;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTMetadata;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.simple.Simple;

import de.xavaro.android.iot.things.IOTLocation;
import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.iot.GetDevices;
import pub.android.interfaces.iot.OnStatusRequest;

public class IOT implements
        SubSystemHandler,
        GetDevices,
        OnStatusRequest
{
    private static final String LOGTAG = IOT.class.getSimpleName();

    public static IOT instance;

    public static IOTBoot boot;
    public static IOTHuman human;
    public static IOTDevice device;
    public static IOTDomain domain;

    public static IOTMessageHandler message;

    public IOTRegister register;

    public IOTProximServer proximServer;
    public IOTProximScanner proximScanner;
    public IOTProximLocation proximLocationListener;

    private Application appcontext;

    public IOT(Application appcontext)
    {
        if (instance == null)
        {
            instance = this;

            this.appcontext = appcontext;
            Simple.initialize(appcontext);
        }
        else
        {
            throw new RuntimeException("IOT system already initialized.");
        }
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "iot");
        Json.put(info, "name", "I.O.T");

        return info;
    }

    @Override
    public void startSubsystem()
    {
        IOTHuman.list = new IOTListGeneric<>((new IOTHuman()).getClassKey());
        IOTDevice.list = new IOTListGeneric<>((new IOTDevice()).getClassKey());
        IOTDomain.list = new IOTListGeneric<>((new IOTDomain()).getClassKey());
        IOTLocation.list = new IOTListGeneric<>((new IOTLocation()).getClassKey());

        IOTStatus.list = new IOTListGeneric<>((new IOTStatus()).getClassKey());
        IOTMetadata.list = new IOTListGeneric<>((new IOTMetadata()).getClassKey());
        IOTCredential.list = new IOTListGeneric<>((new IOTCredential()).getClassKey());

        register = new IOTRegister();

        IOTBoot.initialize();

        IOTService.startService(appcontext);

        IOTMessageHandler.initialize();

        IOTProximServer.startService(appcontext);

        IOTProximScanner.startService(appcontext);

        IOTProximLocation.startService(appcontext);
        IOTAlive.startService();

        IOTHandleHelo.sendHELO();

        onSubsystemStarted("iot", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem()
    {
        IOTAlive.stopService();
        IOTProximLocation.stopService();

        IOTHuman.list = null;
        IOTDevice.list = null;
        IOTDomain.list = null;
        IOTLocation.list = null;

        IOTStatus.list = null;
        IOTMetadata.list = null;
        IOTCredential.list = null;

        onSubsystemStopped("iot", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
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

    @Override
    public boolean onDeviceStatusRequest(JSONObject iotDevice)
    {
        Log.d(LOGTAG, "onDeviceStatusRequest: STUB!");

        return false;
    }

    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));

        return false;
    }

    @Override
    public JSONArray getDeviceWithCapability(String capability)
    {
        JSONArray result = new JSONArray();

        JSONArray list = IOTDevice.list.getListUUIDs();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            if (uuid == null) continue;

            IOTDevice device = IOTDevice.list.getEntryInternal(uuid);
            if (device == null) continue;

            if (device.hasCapability(capability))
            {
                Json.put(result, uuid);
            }
        }

        return result;
    }
}
