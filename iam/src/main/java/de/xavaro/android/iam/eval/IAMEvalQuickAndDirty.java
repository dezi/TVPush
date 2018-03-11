package de.xavaro.android.iam.eval;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iam.simple.Json;

public class IAMEvalQuickAndDirty
{
    @Nullable
    public static JSONObject evaluateSpeech(JSONObject speech)
    {
        JSONArray results = Json.getArray(speech, "results");
        if ((results == null) || (results.length() == 0)) return null;

        JSONObject result = Json.getObject(results, 0);
        String text = Json.getString(result, "text");
        if (text == null) return null;

        JSONObject actions = null;

        if (text.equals("Spracherkennung anzeigen")
                || text.equals("Spracherkennung einblenden"))
        {
            actions = new JSONObject();

            Json.put(actions, "action", "displaySpeechRecognition");
            Json.put(actions, "param", true);
        }

        if (text.equals("Spracherkennung ausblenden")
                || text.equals("Spracherkennung weg"))
        {
            actions = new JSONObject();

            Json.put(actions, "action", "displaySpeechRecognition");
            Json.put(actions, "param", false);
        }

        return actions;
    }
}
