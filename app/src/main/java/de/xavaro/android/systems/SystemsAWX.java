package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.awx.base.AWX;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;

public class SystemsAWX extends AWX
{
    private static final String LOGTAG = SystemsAWX.class.getSimpleName();

    public SystemsAWX(Application application)
    {
        super(application);
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        return GUI.instance.subSystems.getSubsystemState(subsystem);
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        GUI.instance.subSystems.setSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        GUI.instance.subSystems.setSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:");

        IOT.instance.register.registerDevice(device);
    }

    @Override
    public void onDeviceStatus(JSONObject status)
    {
        Log.d(LOGTAG, "onDeviceStatus:");

        IOT.instance.register.registerDeviceStatus(status);
    }

    @Override
    public void onDeviceMetadata(JSONObject metatdata)
    {
        Log.d(LOGTAG, "onDeviceMetadata:");

        IOT.instance.register.registerDeviceMetadata(metatdata);
    }

    @Override
    public void onDeviceCredentials(JSONObject credentials)
    {
        Log.d(LOGTAG, "onDeviceCredentials:");

        IOT.instance.register.registerDeviceCredentials(credentials);
    }

    //region GetDevicesRequest

    @Override
    public JSONObject onGetDeviceRequest(String uuid)
    {
        return IOT.instance.getDevice(uuid);
    }

    @Override
    public JSONObject onGetStatusRequest(String uuid)
    {
        return IOT.instance.getStatus(uuid);
    }

    @Override
    public JSONObject onGetCredentialRequest(String uuid)
    {
        return IOT.instance.getCredential(uuid);
    }

    @Override
    public JSONObject onGetMetaRequest(String uuid)
    {
        return IOT.instance.getMetadata(uuid);
    }

    @Override
    public JSONArray onGetDevicesCapabilityRequest(String capability)
    {
        return IOT.instance.getDevicesWithCapability(capability);
    }

    //endregion GetDevicesRequest
}
