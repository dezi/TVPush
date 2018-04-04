package pub.android.interfaces.iot;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public interface OnDeviceRequest
{
    @Nullable
    JSONObject onDeviceRequest(String uuid);

    @Nullable
    JSONArray onDeviceCapabilityRequest(String capability);
}
