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

        //SNYRemote.sendRemoteCommand("192.168.0.9", "18DF5D5C3B06220A1D6186896BC1462CB2F74616", "Num7");

        //SNYPrograms.importSDB();
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
        Log.d(LOGTAG, "doSomething: device=" + Json.toPretty(device));
        Log.d(LOGTAG, "doSomething: status=" + Json.toPretty(status));
        Log.d(LOGTAG, "doSomething: credentials=" + Json.toPretty(credentials));

        String channel = Json.getString(action, "target");
        String ipaddr = Json.getString(status, "ipaddr");
        String authtoken = "18DF5D5C3B06220A1D6186896BC1462CB2F74616";

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


        return false;
    }
}