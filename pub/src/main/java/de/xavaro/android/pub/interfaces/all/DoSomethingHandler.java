package de.xavaro.android.pub.interfaces.all;

import org.json.JSONObject;

public interface DoSomethingHandler
{
    boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials);
}
