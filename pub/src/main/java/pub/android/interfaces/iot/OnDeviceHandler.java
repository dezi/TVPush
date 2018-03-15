package pub.android.interfaces.iot;

import org.json.JSONObject;

public interface OnDeviceHandler
{
    void onDeviceFound(JSONObject device);
    void onDeviceStatus(JSONObject status);
    void onDeviceMetadata(JSONObject metadata);
    void onDeviceCredentials(JSONObject credentials);
}
