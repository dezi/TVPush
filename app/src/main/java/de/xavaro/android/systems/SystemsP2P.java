package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUISubSystems;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;
import zz.top.p2p.base.P2P;

public class SystemsP2P extends P2P
{
    private static final String LOGTAG = SystemsP2P.class.getSimpleName();

    public SystemsP2P(Application application)
    {
        super(application);

        GUI.instance.subSystems.registerSubsystem(getSubsystemInfo());

        if (GUI.instance.subSystems.getSubsystemState("p2p")
                == GUISubSystems.SUBSYSTEM_STATE_ACTIVATED)
        {
            startSubsystem();
        }
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        GUI.instance.subSystems.registerSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        GUI.instance.subSystems.registerSubsystemRunstate(subsystem, state);
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
}
