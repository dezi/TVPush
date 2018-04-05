package pub.android.interfaces.iot;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBADBTool;

public interface OnADBToolHandlerRequest
{
    @Nullable
    PUBADBTool onADBToolHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials);
}
