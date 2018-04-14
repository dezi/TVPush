package de.xavaro.android.pub.interfaces.ext;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBSmartPlug;

public interface GetSmartPlugHandler
{
    PUBSmartPlug getSmartPlugHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
