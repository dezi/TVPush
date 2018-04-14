package de.xavaro.android.pub.interfaces.iot;

import org.json.JSONObject;

public interface OnStatusRequest
{
    boolean onDeviceStatusRequest(JSONObject device);
}
