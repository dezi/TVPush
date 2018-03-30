package de.xavaro.android.iam.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iam.eval.IAMEval;
import de.xavaro.android.iam.simple.Json;

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
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "iam");
        Json.put(info, "name", "I.A.M - KI");

        return info;
    }

    @Override
    public void startSubsystem()
    {
        Log.d(LOGTAG, "Subsystem started...");

        onSubsystemStarted("iam", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem()
    {
        Log.d(LOGTAG, "Subsystem stopped...");

        onSubsystemStopped("iam", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
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
        Log.d(LOGTAG, "onActionsFound: actions=" + Json.toPretty(actions));
    }

    @Override
    public void evaluateSpeech(JSONObject speech)
    {
        Log.d(LOGTAG, "evaluateSpeech: speech=" + speech.toString());

        JSONArray actions = IAMEval.evaluateSpeech(speech);
        if (actions != null) onActionsFound(actions);
    }
}
