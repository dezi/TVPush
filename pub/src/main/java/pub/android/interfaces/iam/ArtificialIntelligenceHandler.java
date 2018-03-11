package pub.android.interfaces.iam;

import org.json.JSONObject;

public interface ArtificialIntelligenceHandler
{
    void onActionsFound(JSONObject actions);

    void evaluateMessage(JSONObject message);
}
