package pub.android.interfaces.ext;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBSmartBulb;

public interface GetSmartBulbHandler
{
    PUBSmartBulb getSmartBulbHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
