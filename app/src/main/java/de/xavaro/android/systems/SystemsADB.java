package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.adb.simple.Log;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.adb.base.ADB;

public class SystemsADB extends ADB
{
    private static final String LOGTAG = SystemsADB.class.getSimpleName();

    public SystemsADB(Application application)
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
    public void setSubsystemState(String subsystem, int state)
    {
        GUI.instance.subSystems.setSubsystemState(subsystem, state);
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
