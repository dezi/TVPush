package pub.android.interfaces.iam;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ArtificialIntelligenceHandler
{
    void evaluateSpeech(JSONObject speech);
    void onActionsFound(JSONArray actions);
}
