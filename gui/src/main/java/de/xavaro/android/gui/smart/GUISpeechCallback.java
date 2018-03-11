package de.xavaro.android.gui.smart;

import org.json.JSONObject;

public interface GUISpeechCallback
{
    void onActivateRemote();

    void onSpeechReady();

    void onSpeechResults(JSONObject results);
}
