package de.xavaro.android.iot.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.comm.IOTMessageHandler;
import de.xavaro.android.iot.handler.IOTHandleHelo;
import de.xavaro.android.iot.proxim.IOTProxim;
import de.xavaro.android.iot.proxim.IOTProximLocation;
import de.xavaro.android.iot.proxim.IOTProximScanner;
import de.xavaro.android.iot.proxim.IOTProximServer;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.simple.Simple;

import pub.android.interfaces.all.SubSystem;
import pub.android.interfaces.iot.GetDevices;
import pub.android.interfaces.iot.OnStatusRequest;

public class IOT implements
        SubSystem,
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

    public IOT(Application appcontext)
    {
        if (instance == null)
        {
            IOT.instance = this;

            register = new IOTRegister();

            Simple.initialize(appcontext);
            Simple.checkBTFeatures(appcontext);

            IOTBoot.initialize();

            IOTService.startService(appcontext);

            IOTMessageHandler.initialize();

            IOTHandleHelo.sendHELO();

            IOTProxim.checkBTPermissions(appcontext);

            IOTProximServer.startService(appcontext);

            IOTProximScanner.startService(appcontext);

            IOTProximLocation.startLocationListener(appcontext);

            IOTAlive.startService();
        }
        else
        {
            throw new RuntimeException("IOT system already initialized.");
        }
    }

    @Override
    public String getDriverTag()
    {
        return "iot";
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

        JSONArray list = IOTDevices.instance.getListUUIDs();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            if (uuid == null) continue;

            IOTDevice device = IOTDevices.getEntry(uuid);
            if (device == null) continue;

            if (device.hasCapability(capability))
            {
                Json.put(result, uuid);
            }
        }

        return result;
    }
}
