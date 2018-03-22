package pub.android.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import pub.android.interfaces.drv.SmartPlug;

public interface OnSmartPlugHandlerRequest
{
    @Nullable
    SmartPlug onSmartPlugHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
