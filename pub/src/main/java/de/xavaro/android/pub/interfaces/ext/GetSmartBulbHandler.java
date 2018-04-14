package de.xavaro.android.pub.interfaces.ext;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBSmartBulb;

public interface GetSmartBulbHandler
{
    PUBSmartBulb getSmartBulbHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
