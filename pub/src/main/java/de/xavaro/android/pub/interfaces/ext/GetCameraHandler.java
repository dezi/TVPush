package de.xavaro.android.pub.interfaces.ext;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBCamera;

public interface GetCameraHandler
{
    PUBCamera getCameraHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
