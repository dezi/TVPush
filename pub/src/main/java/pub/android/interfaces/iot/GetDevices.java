package pub.android.interfaces.iot;

import org.json.JSONArray;

public interface GetDevices
{
    JSONArray getDeviceWithCapability(String capability);
}
