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
        instance = this;

        Simple.initialize(application);

        SNYDiscover.discover(10);
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
    public void onDeviceMetadata(JSONObject metadata)
    {
        Log.d(LOGTAG, "onDeviceMetadata: STUB!");
    }

    @Override
    public void onDeviceCredentials(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceCredentials: STUB!");
    }

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        /*
        Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));
        Log.d(LOGTAG, "doSomething: device=" + Json.toPretty(device));
        Log.d(LOGTAG, "doSomething: status=" + Json.toPretty(status));
        Log.d(LOGTAG, "doSomething: credentials=" + Json.toPretty(credentials));
        */

        String actioncmd = Json.getString(action, "action");

        if ((actioncmd != null) && actioncmd.equals("select"))
        {
            String ipaddr = Json.getString(status, "ipaddr");
            String channel = Json.getString(action, "actionData");
            String authtoken = Json.getString(credentials, "authtoken");

            if (channel != null)
            {
                if (channel.equals("RTL"))
                {
                    SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num3");
                }

                if (channel.equals("ARD"))
                {
                    SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num1");
                }

                if (channel.equals("ZDF"))
                {
                    SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num2");
                }

                if (channel.equals("RTL2"))
                {
                    SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num0");
                    SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num0");
                    SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num6");
                }
            }
        }

        return false;
    }
}