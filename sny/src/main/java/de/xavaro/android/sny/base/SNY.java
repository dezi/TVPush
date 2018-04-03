package de.xavaro.android.sny.base;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.stubs.OnInterfacesStubs;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.all.DoSomethingHandler;
import pub.android.interfaces.ext.GetDeviceCredentials;
import pub.android.interfaces.ext.OnBackgroundRequest;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.ext.OnPincodeRequest;

import de.xavaro.android.sny.simple.Simple;
import de.xavaro.android.sny.simple.Json;
import de.xavaro.android.sny.simple.Log;
import de.xavaro.android.sny.R;

public class SNY extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDeviceCredentials,
        DoSomethingHandler,
        OnPincodeRequest,
        OnBackgroundRequest
{
    private static final String LOGTAG = SNY.class.getSimpleName();

    public static SNY instance;

    public SNYRemote remote;
    public SNYDiscover discover;

    public SNY(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "sny");

        Json.put(info, "name", Simple.getTrans(R.string.subsystem_sny_name));
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_sny_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_sony_600));

        JSONArray settings = new JSONArray();
        Json.put(info, "settings", settings);

        JSONObject tvremote = new JSONObject();

        Json.put(tvremote, "tag", "tvremote");
        Json.put(tvremote, "name", Simple.getTrans(R.string.subsystem_service_tvremote_name));
        Json.put(tvremote, "type", SubSystemHandler.SUBSYSTEM_TYPE_SERVICE);
        Json.put(tvremote, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(tvremote, "info", Simple.getTrans(R.string.subsystem_service_tvremote_info));
        Json.put(tvremote, "icon", Simple.getImageResourceBase64(R.drawable.device_tvremote_550));
        Json.put(tvremote, "need", "pin");

        Json.put(settings, tvremote);

        JSONObject channeledit = new JSONObject();

        Json.put(channeledit, "tag", "channeledit");
        Json.put(channeledit, "name", Simple.getTrans(R.string.subsystem_service_channeledit_name));
        Json.put(channeledit, "type", SubSystemHandler.SUBSYSTEM_TYPE_FEATURE);
        Json.put(channeledit, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(channeledit, "info", Simple.getTrans(R.string.subsystem_service_channeledit_info));
        Json.put(channeledit, "icon", Simple.getImageResourceBase64(R.drawable.wizzard_channeledit_440));
        Json.put(channeledit, "need", "ext+usb+dev+adb");

        Json.put(settings, channeledit);

        return info;
    }

    @Override
    public void startSubsystem()
    {
        if (onGetSubsystemState("sny") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            SNYDiscover.startService();
            onSubsystemStarted("sny", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
        }

        if (onGetSubsystemState("sny.tvremote") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            SNYRemote.startService();
            onSubsystemStarted("sny.tvremote", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
        }
    }

    @Override
    public void stopSubsystem()
    {
        SNYDiscover.stopService();
        onSubsystemStopped("sny", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);

        SNYRemote.stopService();
        onSubsystemStopped("sny.tvremote", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    @Override
    public JSONObject getDeviceCredentials(String uuid)
    {
        Log.d(LOGTAG, "getDeviceCredentials: STUB!");

        return null;
    }

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));
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
                        SNYRemote remote = SNY.instance.remote;

                        if (remote == null) return;

                        for (int inx = 0; inx < dial.length(); inx++)
                        {
                            char digit = dial.charAt(inx);

                            switch (digit)
                            {
                                case '0': remote.sendRemoteCommand(ipaddr, authtoken, "Num0"); break;
                                case '1': remote.sendRemoteCommand(ipaddr, authtoken, "Num1"); break;
                                case '2': remote.sendRemoteCommand(ipaddr, authtoken, "Num2"); break;
                                case '3': remote.sendRemoteCommand(ipaddr, authtoken, "Num3"); break;
                                case '4': remote.sendRemoteCommand(ipaddr, authtoken, "Num4"); break;
                                case '5': remote.sendRemoteCommand(ipaddr, authtoken, "Num5"); break;
                                case '6': remote.sendRemoteCommand(ipaddr, authtoken, "Num6"); break;
                                case '7': remote.sendRemoteCommand(ipaddr, authtoken, "Num7"); break;
                                case '8': remote.sendRemoteCommand(ipaddr, authtoken, "Num8"); break;
                                case '9': remote.sendRemoteCommand(ipaddr, authtoken, "Num9"); break;
                            }
                        }

                        remote.sendRemoteCommand(ipaddr, authtoken, "DpadCenter");
                    }

                }, 1000);

                return true;
            }
        }

        return false;
    }
}