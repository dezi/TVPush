package pub.android.interfaces.ext;

import org.json.JSONObject;

public interface OnSpeechHandler
{
    void onActivateRemote();

    void onSpeechReady();

    void onSpeechResults(JSONObject results);
}
