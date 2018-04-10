package pub.android.interfaces.iot;

import org.json.JSONObject;

public interface OnStatusRequest
{
    boolean onDeviceStatusRequest(JSONObject device);
}
