package de.xavaro.android.pub.interfaces.iot;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBADBTool;

public interface OnADBToolHandlerRequest
{
    @Nullable
    PUBADBTool onADBToolHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
