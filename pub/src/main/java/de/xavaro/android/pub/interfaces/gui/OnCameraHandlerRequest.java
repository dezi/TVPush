package de.xavaro.android.pub.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBCamera;

public interface OnCameraHandlerRequest
{
    @Nullable
    PUBCamera onCameraHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
