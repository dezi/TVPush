package zz.top.p2p.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import pub.android.interfaces.iot.InternetOfThingsHandler;

import zz.top.p2p.camera.P2PCloud;
import zz.top.utl.Json;
import zz.top.utl.Simple;

public class P2P implements InternetOfThingsHandler
{
    private static final String LOGTAG = P2P.class.getSimpleName();

    public P2PCloud cloud;

    public P2P(Application application)
    {
        Simple.initialize(application);

        cloud = new P2PCloud(this);
    }

    public void login(String email, String password)
    {
        cloud.login(email, password);
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound: STUB!");
    }

    @Override
    public void onDeviceStatus(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceStatus: STUB!");
    }

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status)
    {
        Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));
        Log.d(LOGTAG, "doSomething: status=" + Json.toPretty(status));

        return false;
    }
}
