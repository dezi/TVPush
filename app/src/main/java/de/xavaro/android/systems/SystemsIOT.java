package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBADBTool;

import de.xavaro.android.adb.base.ADB;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;

public class SystemsIOT extends IOT
{
    private static final String LOGTAG = SystemsIOT.class.getSimpleName();

    public SystemsIOT(Application appcontext)
    {
        super(appcontext);
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
    public PUBADBTool onADBToolHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        if (ADB.instance != null)
        {
            return ADB.instance.getADBToolHandler(device, status, credentials);
        }

        return null;
    }

    @Override
    public boolean onDeviceStatusRequest(JSONObject iotDevice)
    {
        String uuid = Json.getString(iotDevice, "uuid");
        String driver = Json.getString(iotDevice, "driver");

        Log.d(LOGTAG, "onDeviceStatusRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return false;

        if (driver.equals("p2p") && (SystemsP2P.instance != null))
        {

        }

        if (driver.equals("tpl") && (SystemsTPL.instance != null))
        {
            return SystemsTPL.instance.putDeviceStatusRequest(iotDevice);
        }

        if (driver.equals("sny") && (SystemsSNY.instance != null))
        {

        }

        return false;
    }
}
