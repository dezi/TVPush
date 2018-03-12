package pub.android.interfaces.iam;

import org.json.JSONArray;
import org.json.JSONObject;

public interface ArtificialIntelligenceHandler
{
    void onActionsFound(JSONArray actions);

    void evaluateSpeech(JSONObject speech);
}
