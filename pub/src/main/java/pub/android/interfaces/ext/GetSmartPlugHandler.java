package pub.android.interfaces.ext;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBSmartPlug;

public interface GetSmartPlugHandler
{
    PUBSmartPlug getSmartPlugHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
