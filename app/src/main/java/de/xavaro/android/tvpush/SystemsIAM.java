package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
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
        Log.d(LOGTAG, "onActionsFound: actions=" + actions.toString());

        String action = Json.getString(actions, "action");
        if (action == null) return;

        if (action.equals("displaySpeechRecognition"))
        {
            boolean show = Json.getBoolean(actions, "param");

            GUI.instance.displaySpeechRecognition(show);

            Log.d(LOGTAG, "onActionsFound: displaySpeechRecognition show=" + show);
        }
    }

    @Override
    public void evaluateSpeech(JSONObject speech)
    {
        Log.d(LOGTAG, "evaluateSpeech: speech=" + speech.toString());

        super.evaluateSpeech(speech);
    }
}
