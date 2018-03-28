package pub.android.interfaces.ext;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBCamera;

public interface GetCameraHandler
{
    PUBCamera getCameraHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
