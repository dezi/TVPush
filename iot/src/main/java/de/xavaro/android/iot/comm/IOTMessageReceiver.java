package de.xavaro.android.iot.comm;

import org.json.JSONObject;

public interface IOTMessageReceiver
{
    void receiveMessage(JSONObject message);
}
