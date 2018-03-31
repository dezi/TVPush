package zz.top.sny.base;

import android.app.Application;

import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.ext.GetDeviceCredentials;
import pub.android.interfaces.ext.OnBackgroundRequest;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.all.DoSomethingHandler;
import pub.android.interfaces.ext.OnPincodeRequest;

import zz.top.utl.Simple;
import zz.top.utl.Json;
import zz.top.utl.Log;

public class SNY implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDeviceCredentials,
        DoSomethingHandler,
        OnPincodeRequest,
        OnBackgroundRequest
{
    private static final String LOGTAG = SNY.class.getSimpleName();

    public static SNY instance;

    public SNY(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "sny");
        Json.put(info, "name", "Sony Android TV");

        return info;
    }

    @Override
    public void startSubsystem()
    {
        Log.d(LOGTAG, "startSubsystem: ######");

        SNYDiscover.discover(10);

        onSubsystemStarted("sny", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem()
    {
        Log.d(LOGTAG, "stopSubsystem: ######");

        onSubsystemStopped("sny", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        android.util.Log.d(LOGTAG, "onSubsystemStarted: STUB! state=" + state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        android.util.Log.d(LOGTAG, "onSubsystemStopped: STUB! state=" + state);
    }

    @Override
    public JSONObject getDeviceCredentials(String uuid)
    {
        Log.d(LOGTAG, "getDeviceCredentials: STUB!");

        return null;
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
    public void onDeviceCredentials(JSONObject credentials)
    {
        Log.d(LOGTAG, "onDeviceCredentials: STUB!");
    }

    @Override
    public void onPincodeRequest(String uuid)
    {
        Log.d(LOGTAG, "onPincodeRequest: STUB!");
    }

    @Override
    public void onBackgroundRequest()
    {
        Log.d(LOGTAG, "onBackgroundRequest: STUB!");
    }

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));
        //Log.d(LOGTAG, "doSomething: device=" + Json.toPretty(device));
        //Log.d(LOGTAG, "doSomething: status=" + Json.toPretty(status));
        Log.d(LOGTAG, "doSomething: credentials=" + Json.toPretty(credentials));

        String actioncmd = Json.getString(action, "action");
        if (actioncmd == null) return false;

        if (actioncmd.equals("pincode"))
        {
            String pincode = Json.getString(action, "actionData");

            Log.d(LOGTAG, "doSomething: pincode=" + pincode);

            return SNYAuthorize.enterPincode(pincode);
        }

        if (actioncmd.equals("select"))
        {
            JSONObject mycredentials = Json.getObject(credentials, "credentials");

            final String dial = Json.getString(action, "actionData");
            final String ipaddr = Json.getString(status, "ipaddr");
            final String authtoken = Json.getString(mycredentials, "authtoken");

            if (dial != null)
            {
                //
                // Request GUI to go to background. Otherwise
                // dialed numbers are lost and the Sony backgrounds
                // the GUI anyway.
                //

                onBackgroundRequest();

                Simple.getHandler().postDelayed(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        for (int inx = 0; inx < dial.length(); inx++)
                        {
                            char digit = dial.charAt(inx);

                            switch (digit)
                            {
                                case '0': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num0"); break;
                                case '1': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num1"); break;
                                case '2': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num2"); break;
                                case '3': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num3"); break;
                                case '4': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num4"); break;
                                case '5': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num5"); break;
                                case '6': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num6"); break;
                                case '7': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num7"); break;
                                case '8': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num8"); break;
                                case '9': SNYRemote.sendRemoteCommand(ipaddr, authtoken, "Num9"); break;
                            }
                        }

                        SNYRemote.sendRemoteCommand(ipaddr, authtoken, "DpadCenter");

                    }
                }, 1000);
            }
        }

        return false;
    }
}