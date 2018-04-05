package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.things.IOTDevice;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.adb.base.ADB;
import de.xavaro.android.iot.base.IOT;

public class SystemsADB extends ADB
{
    private static final String LOGTAG = SystemsADB.class.getSimpleName();

    public SystemsADB(Application application)
    {
        super(application);

        GUI.instance.subSystems.registerSubsystem(getSubsystemInfo());
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
    public JSONObject onDeviceRequest(String uuid)
    {
        IOTDevice device = IOTDevice.list.getEntry(uuid);
        return (device != null) ? device.toJson() : null;
    }

    @Override
    public JSONArray onDeviceCapabilityRequest(String capability)
    {
        return IOT.instance.getDeviceWithCapability(capability);
    }
}
