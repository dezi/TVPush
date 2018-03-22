package pub.android.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import pub.android.interfaces.drv.Camera;

public interface OnCameraHandlerRequest
{
    @Nullable
    Camera onCameraHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
