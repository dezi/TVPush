package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.spr.base.SPR;
import de.xavaro.android.spr.simple.Log;

public class SystemsSPR extends SPR
{
    private static final String LOGTAG = SystemsSPR.class.getSimpleName();

    public SystemsSPR(Application application)
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
    public void onActivateRemote()
    {
        GUI.instance.onActivateRemote();
    }

    @Override
    public void onSpeechReady()
    {
        GUI.instance.onSpeechReady();
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        GUI.instance.onSpeechResults(speech);
    }
}
