package pub.android.interfaces.iot;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public interface GetDevices
{
    @Nullable
    JSONObject getDevice(String uuid);

    @Nullable
    JSONArray getDevicesWithCapability(String capability);
}
