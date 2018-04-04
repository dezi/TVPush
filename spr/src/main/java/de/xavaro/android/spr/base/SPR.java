package de.xavaro.android.spr.base;

import android.app.Application;

import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;

import de.xavaro.android.spr.simple.Simple;
import de.xavaro.android.spr.simple.Json;
import de.xavaro.android.spr.simple.Log;
import de.xavaro.android.spr.R;

public class SPR implements SubSystemHandler
{
    private static final String LOGTAG = SPR.class.getSimpleName();

    public static SPR instance;

    public SPR(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "sny");

        Json.put(info, "name", Simple.getTrans(R.string.subsystem_spr_name));
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_spr_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_speechrec_170));

        return info;
    }

    @Override
    public void startSubsystem()
    {
        if (onGetSubsystemState("spr") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            //SNYDiscover.startService();
            onSubsystemStarted("spr", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
        }
    }

    @Override
    public void stopSubsystem()
    {
        if (onGetSubsystemState("spr") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
        {
            //SNYDiscover.stopService();
            onSubsystemStopped("spr", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
        }
    }

    public int onGetSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "onGetSubsystemState: STUB!");

        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    public void onSubsystemStarted(String subsystem, int runstate)
    {
        Log.d(LOGTAG, "onSubsystemStarted: STUB!");
    }

    public void onSubsystemStopped(String subsystem, int runstate)
    {
        Log.d(LOGTAG, "onSubsystemStopped: STUB!");
    }
}