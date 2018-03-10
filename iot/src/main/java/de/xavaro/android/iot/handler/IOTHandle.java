package de.xavaro.android.iot.handler;

import org.json.JSONObject;

public abstract class IOTHandle
{
    public abstract void onMessageReived(JSONObject message);
}
