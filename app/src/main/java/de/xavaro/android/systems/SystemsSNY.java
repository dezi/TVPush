package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.sny.base.SNY;

public class SystemsSNY extends SNY
{
    private static final String LOGTAG = SystemsSNY.class.getSimpleName();

    public SystemsSNY(Application application)
    {
        super(application);

        GUI.instance.subSystems.registerSubsystem(getSubsystemInfo());
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
        Log.d(LOGTAG, "onDeviceFound:" + Json.toPretty(device));

        IOT.instance.register.registerDevice(device);
    }

    @Override
    public void onDeviceStatus(JSONObject status)
    {
        Log.d(LOGTAG, "onDeviceStatus:" + Json.toPretty(status));

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
        Log.d(LOGTAG, "onDeviceCredentials:" + Json.toPretty(credentials));

        IOT.instance.register.registerDeviceCredentials(credentials);
    }

    @Override
    public void onPincodeRequest(String uuid)
    {
        Log.d(LOGTAG, "onPincodeRequest: uuid=" + uuid);

        if (GUI.instance != null)
        {
            Simple.getHandler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    GUI.instance.displayPinCodeMessage(60);
                }
            }, 1000);
        }
    }

    @Override
    public void onBackgroundRequest()
    {
        GUI.instance.desktopActivity.sendToBack();
    }
}
