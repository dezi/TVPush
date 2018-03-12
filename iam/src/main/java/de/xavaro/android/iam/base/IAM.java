package de.xavaro.android.iam.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iam.eval.IAMEval;
import de.xavaro.android.iam.simple.Json;

import pub.android.interfaces.iam.ArtificialIntelligenceHandler;

public class IAM implements ArtificialIntelligenceHandler
{
    private static final String LOGTAG = IAM.class.getSimpleName();

    public static IAM instance;

    public IAM(Application appcontext)
    {
        if (instance == null)
        {
            instance = this;
        }
        else
        {
            throw new RuntimeException("IAM system already initialized.");
        }
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
