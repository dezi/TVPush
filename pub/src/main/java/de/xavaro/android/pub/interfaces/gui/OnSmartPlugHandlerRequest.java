package de.xavaro.android.pub.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBSmartPlug;

public interface OnSmartPlugHandlerRequest
{
    @Nullable
    PUBSmartPlug onSmartPlugHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
