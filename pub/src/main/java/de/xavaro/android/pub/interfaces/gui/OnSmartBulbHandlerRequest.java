package de.xavaro.android.pub.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBSmartBulb;

public interface OnSmartBulbHandlerRequest
{
    @Nullable
    PUBSmartBulb onSmartBulbHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
