package pub.android.interfaces.iot;

import org.json.JSONObject;

public interface InternetOfThingsHandler
{
    void onDeviceFound(JSONObject device);
    void onDeviceStatus(JSONObject status);
    void onDeviceCredentials(JSONObject credentials);

    boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials);
}
