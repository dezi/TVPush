package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.bcn.base.BCN;

public class SystemsBCN extends BCN
{
    public SystemsBCN(Application application)
    {
        super(application);
    }

    //region SubSystemHandler

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

    //endregion SubSystemHandler

    //region OnDeviceHandler

    @Override
    public void onDeviceFound(JSONObject device)
    {
        IOT.instance.register.registerDevice(device);
    }

    @Override
    public void onDeviceStatus(JSONObject status)
    {
        IOT.instance.register.registerDeviceStatus(status);
    }

    //endregion OnDeviceHandler

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
