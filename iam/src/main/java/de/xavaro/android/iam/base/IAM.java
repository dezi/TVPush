package de.xavaro.android.iam.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iam.eval.IAMEval;
import de.xavaro.android.iam.simple.Simple;
import de.xavaro.android.iam.simple.Json;
import de.xavaro.android.iam.R;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.iam.ArtificialIntelligenceHandler;

public class IAM implements
        SubSystemHandler,
        ArtificialIntelligenceHandler
{
    private static final String LOGTAG = IAM.class.getSimpleName();

    public static IAM instance;

    public IAM(Application appcontext)
    {
        Simple.initialize(appcontext);
    }

    @Override
    public void setInstance()
    {
        IAM.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "iam");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_iam_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_iam_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_iam_220));

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
        Log.d(LOGTAG, "Subsystem started...");

        onSubsystemStarted("iam", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        Log.d(LOGTAG, "Subsystem stopped...");

        onSubsystemStopped("iam", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG, "getSubsystemState: STUB! subsystem=" + subsystem);
        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    @Override
    public void setSubsystemState(String subsystem, int state)
    {
        Log.d(LOGTAG, "setSubsystemState: STUB! state=" + state);
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStarted: STUB! state=" + state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStopped: STUB! state=" + state);
    }

    @Override
    public void onActionsFound(JSONArray actions)
    {
        Log.d(LOGTAG, "onActionsFound: STUB! actions=" + Json.toPretty(actions));
    }

    @Override
    public void evaluateSpeech(JSONObject speech)
    {
        Log.d(LOGTAG, "evaluateSpeech: speech=" + speech.toString());

        JSONArray actions = IAMEval.evaluateSpeech(speech);
        if (actions != null) onActionsFound(actions);
    }
}
