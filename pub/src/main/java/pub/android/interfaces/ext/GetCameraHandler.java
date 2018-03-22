package pub.android.interfaces.ext;

import org.json.JSONObject;

import pub.android.interfaces.drv.Camera;

public interface GetCameraHandler
{
    Camera getCameraHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
