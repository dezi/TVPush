package pub.android.interfaces.iot;

import android.support.annotation.Nullable;

import org.json.JSONObject;

public interface GetDeviceCredentials
{
    @Nullable
    JSONObject getDeviceCredentials(String uuid);
}
