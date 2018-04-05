package pub.android.interfaces.ext;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBADBTool;

public interface GetADBToolHandler
{
    PUBADBTool getADBToolHandler(JSONObject device, JSONObject status, JSONObject credentials);
}
