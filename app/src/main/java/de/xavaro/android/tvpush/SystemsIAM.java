package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iam.base.IAM;
import zz.top.utl.Json;

public class SystemsIAM extends IAM
{
    private static final String LOGTAG = SystemsIAM.class.getSimpleName();

    public SystemsIAM(Application application)
    {
        super(application);
    }

    @Override
    public void onActionsFound(JSONObject actions)
    {
        Log.d(LOGTAG, "onActionsFound: actions=" + Json.toPretty(actions));
    }

    @Override
    public void evaluateSpeech(JSONObject speech)
    {
        Log.d(LOGTAG, "evaluateSpeech: speech=" + speech.toString());
    }
}
