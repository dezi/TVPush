package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
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
    public void onActionsFound(JSONArray actions)
    {
        if ((actions == null) || (actions.length() == 0))
        {
            Log.d(LOGTAG, "onActionsFound: no actions found.");
            return;
        }

        Log.d(LOGTAG, "onActionsFound: actions=");
        Log.d(LOGTAG, Json.toPretty(actions));

        for (int inx = 0; inx < actions.length(); inx++)
        {
            JSONObject jaction = Json.getObject(actions, inx);
            if (jaction == null) continue;

            String action = Json.getString(jaction, "action");
            if (action == null) continue;

            String object = Json.getString(jaction, "object");

            if (object != null)
            {
                if (object.equals("speechlistener"))
                {
                    if (action.equals("open"))
                    {
                        GUI.instance.displaySpeechRecognition(true);
                    }

                    if (action.equals("close"))
                    {
                        GUI.instance.displaySpeechRecognition(false);
                    }
                }
            }
        }
    }

    @Override
    public void evaluateSpeech(JSONObject speech)
    {
        Log.d(LOGTAG, "evaluateSpeech: speech=" + speech.toString());

        super.evaluateSpeech(speech);
    }
}
