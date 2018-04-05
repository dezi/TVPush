package pub.android.interfaces.iot;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public interface GetDevices
{
    @Nullable
    JSONObject getDevice(String uuid);

    @Nullable
    JSONObject getStatus(String uuid);

    @Nullable
    JSONObject getCredential(String uuid);

    @Nullable
    JSONObject getMetadata(String uuid);

    @Nullable
    JSONArray getDevicesWithCapability(String capability);
}
