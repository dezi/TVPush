package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBADBTool;

import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;

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
    public boolean onDeviceStatusRequest(JSONObject device)
    {
        String uuid = Json.getString(device, "uuid");
        String driver = Json.getString(device, "driver");
        String nick = Json.getString(device, "nick");

        Log.d(LOGTAG, "onDeviceStatusRequest: uuid=" + uuid + " driver=" + driver + " nick=" + nick);

        if ((uuid == null) || (driver == null)) return false;

        JSONObject status = IOTStatus.list.getEntryJson(uuid);
        JSONObject credential = IOTCredential.list.getEntryJson(uuid);

        if (driver.equals("brl") && (SystemsBRL.instance != null))
        {
            return SystemsBRL.instance.getDeviceStatusRequest(device, status, credential);
        }

        if (driver.equals("tpl") && (SystemsTPL.instance != null))
        {
            return SystemsTPL.instance.getDeviceStatusRequest(device, status, credential);
        }

        if (driver.equals("edx") && (SystemsEDX.instance != null))
        {
            return SystemsEDX.instance.getDeviceStatusRequest(device, status, credential);
        }

        if (driver.equals("awx") && (SystemsAWX.instance != null))
        {
            return SystemsAWX.instance.getDeviceStatusRequest(device, status, credential);
        }

        return false;
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        GUI.instance.onSpeechResults(speech);
    }
}
