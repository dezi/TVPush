package pub.android.interfaces.ext;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public interface GetDevicesRequest
{
    @Nullable
    JSONObject onGetDeviceRequest(String uuid);

    @Nullable
    JSONArray onGetDevicesCapabilityRequest(String capability);
}
