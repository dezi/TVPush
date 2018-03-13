package pub.android.interfaces.iot;

import org.json.JSONObject;

public interface InternetOfThingsHandler
{
    void onDeviceFound(JSONObject device);
    void onDeviceStatus(JSONObject device);

    boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials);
}
