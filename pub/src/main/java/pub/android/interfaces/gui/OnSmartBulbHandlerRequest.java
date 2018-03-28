package pub.android.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBSmartBulb;

public interface OnSmartBulbHandlerRequest
{
    @Nullable
    PUBSmartBulb onSmartBulbHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
