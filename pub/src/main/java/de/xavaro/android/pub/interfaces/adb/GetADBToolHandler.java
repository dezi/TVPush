package de.xavaro.android.pub.interfaces.adb;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBADBTool;

public interface GetADBToolHandler
{
    PUBADBTool getADBToolHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
