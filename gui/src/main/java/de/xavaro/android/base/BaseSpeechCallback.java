package de.xavaro.android.base;

import org.json.JSONObject;

public interface BaseSpeechCallback
{
    void onActivateRemote();

    void onSpeechReady();

    void onSpeechResults(JSONObject results);
}
