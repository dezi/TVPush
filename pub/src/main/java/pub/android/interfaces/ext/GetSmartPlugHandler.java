package pub.android.interfaces.ext;

import org.json.JSONObject;

import pub.android.interfaces.drv.SmartPlug;

public interface GetSmartPlugHandler
{
    SmartPlug getSmartPlugHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
