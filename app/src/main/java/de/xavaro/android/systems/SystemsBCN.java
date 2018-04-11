package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.bcn.base.BCN;

public class SystemsBCN extends BCN
{
    private static final String LOGTAG = SystemsBCN.class.getSimpleName();

    public SystemsBCN(Application application)
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

    //region OnLocationHandler

    @Override
    public void onLocationMeasurement(JSONObject measurement)
    {
        IOT.instance.proximLocationListener.addLocationMeasurement(measurement);
    }

    //endregion OnLocationHandler

    //region OnAliveHandler

    @Override
    public void onThingAlive(String uuid)
    {
        IOT.instance.alive.setAliveNetwork(uuid);
    }

    //endregion OnAliveHandler

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
