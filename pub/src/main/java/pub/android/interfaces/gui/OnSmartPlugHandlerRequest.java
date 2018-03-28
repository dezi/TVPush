package pub.android.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBSmartPlug;

public interface OnSmartPlugHandlerRequest
{
    @Nullable
    PUBSmartPlug onSmartPlugHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
