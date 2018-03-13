package zz.top.sny.base;

import android.app.Application;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONObject;

import pub.android.interfaces.iot.InternetOfThingsHandler;
import zz.top.utl.Json;
import zz.top.utl.Simple;

public class SNY implements InternetOfThingsHandler
{
    private static final String LOGTAG = SNY.class.getSimpleName();

    public static SNY instance;

    public SNY(Application application)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        instance = this;

        Simple.initialize(application);

        //SNYDiscover.discover(10);

        //SNYAuthorize.authorize("192.168.0.9", "035FC338-B56D-4418-9163-42657A17AFFE", "Dezi-Phone", "dezi");

        SNYRemote.sendRemoteCommand("192.168.0.9", "18DF5D5C3B06220A1D6186896BC1462CB2F74616", "Num7");

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
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));

        return false;
    }
}