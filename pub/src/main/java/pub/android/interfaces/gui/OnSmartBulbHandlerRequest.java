package pub.android.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import pub.android.interfaces.drv.SmartBulb;

public interface OnSmartBulbHandlerRequest
{
    @Nullable
    SmartBulb onSmartBulbHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
