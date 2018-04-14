package de.xavaro.android.pub.interfaces.iam;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ArtificialIntelligenceHandler
{
    void evaluateSpeech(JSONObject speech);
    void onActionsFound(JSONArray actions);
}
