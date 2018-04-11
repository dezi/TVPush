package pub.android.interfaces.ext;

import org.json.JSONObject;

public interface GetDeviceStatusRequest
{
    void discoverDevicesRequest();

    boolean getDeviceStatusRequest(JSONObject device, JSONObject status, JSONObject credential);
}
