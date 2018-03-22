package pub.android.interfaces.ext;

import org.json.JSONObject;

import pub.android.interfaces.drv.SmartBulb;

public interface GetSmartBulbHandler
{
    SmartBulb getSmartBulbHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
