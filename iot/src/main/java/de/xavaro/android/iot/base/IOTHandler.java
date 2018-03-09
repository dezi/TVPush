package de.xavaro.android.iot.base;

import org.json.JSONObject;

public abstract class IOTHandler
{
    public abstract void onMessageReived(JSONObject message);
}
