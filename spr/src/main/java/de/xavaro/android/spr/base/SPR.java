package de.xavaro.android.spr.base;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.all.SubSystemHandler;

import de.xavaro.android.pub.interfaces.ext.GetSpeechListenerHandler;
import de.xavaro.android.pub.interfaces.pub.PUBSpeechListener;
import de.xavaro.android.spr.simple.Simple;
import de.xavaro.android.spr.simple.Json;
import de.xavaro.android.spr.simple.Log;
import de.xavaro.android.spr.R;
import de.xavaro.android.pub.interfaces.ext.OnSpeechHandler;

public class SPR implements
        SubSystemHandler,
        OnSpeechHandler,
        GetSpeechListenerHandler
{
    private static final String LOGTAG = SPR.class.getSimpleName();

    public static SPR instance;

    public Application appcontext;
    public SPRListener sprListener;

    public SPR(Application application)
    {
        appcontext = application;
        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        SPR.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        int mode = Simple.isSpeechIn()
                ? SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY
                : SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE
                ;

        JSONObject info = new JSONObject();

        Json.put(info, "drv", "spr");

        Json.put(info, "name", Simple.getTrans(R.string.subsystem_spr_name));
        Json.put(info, "mode", mode);
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_spr_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_speechrec_170));
        Json.put(info, "need", "mic+sro");

        return info;
    }

    @Override
    public JSONObject getSubsystemSettings()
    {
        return getSubsystemInfo();
    }

    @Override
    public void startSubsystem(String subsystem)
    {
        if (getSubsystemState("spr") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            SPRListener.startService(appcontext);

            onSubsystemStarted("spr", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
        }
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        if (getSubsystemState("spr") == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
        {
            SPRListener.stopService();

            onSubsystemStopped("spr", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
        }
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "getSubsystemState: STUB!");

        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    @Override
    public void setSubsystemState(String subsystem, int state)
    {
        Log.d(LOGTAG, "setSubsystemState: STUB! state=" + state);
    }

    @Override
    public void onSubsystemStarted(String subsystem, int runstate)
    {
        Log.d(LOGTAG, "onSubsystemStarted: STUB!");
    }

    @Override
    public void onSubsystemStopped(String subsystem, int runstate)
    {
        Log.d(LOGTAG, "onSubsystemStopped: STUB!");
    }

    //endregion SubSystemHandler

    //region OnSpeechHandler

    @Override
    public void onActivateRemote()
    {
        Log.d(LOGTAG, "onActivateRemote: STUB!");
    }

    @Override
    public void onSpeechReady()
    {
        Log.d(LOGTAG, "onSpeechReady: STUB!");
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: STUB!");
    }

    //endregion OnSpeechHandler

    //region GetSpeechListenerHandler

    @Override
    public PUBSpeechListener getSpeechListenerHandler()
    {
        return sprListener;
    }

    //endregion GetSpeechListenerHandler
}