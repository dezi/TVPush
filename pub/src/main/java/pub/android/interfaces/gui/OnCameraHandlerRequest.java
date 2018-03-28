package pub.android.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBCamera;

public interface OnCameraHandlerRequest
{
    @Nullable
    PUBCamera onCameraHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
