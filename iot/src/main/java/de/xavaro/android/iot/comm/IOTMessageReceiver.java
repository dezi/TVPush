package de.xavaro.android.iot.comm;

import org.json.JSONObject;

public interface IOTMessageReceiver
{
    JSONObject receiveMessage(JSONObject message);
}
