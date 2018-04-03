package de.xavaro.android.sny.base;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.all.DoSomethingHandler;
import pub.android.interfaces.ext.GetDeviceCredentials;
import pub.android.interfaces.ext.OnBackgroundRequest;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.ext.OnPincodeRequest;
import pub.android.stubs.OnInterfacesStubs;

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
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_sny_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_sony_600));

        JSONArray services = new JSONArray();
        Json.put(info, "services", services);

        JSONObject tvremote = new JSONObject();

        Json.put(tvremote, "name", Simple.getTrans(R.string.subsystem_service_tvremote_name));
        Json.put(tvremote, "info", Simple.getTrans(R.string.subsystem_service_tvremote_info));

        Json.put(services, tvremote);

        JSONObject channeledit = new JSONObject();

        Json.put(channeledit, "name", Simple.getTrans(R.string.subsystem_service_channeledit_name));
        Json.put(channeledit, "info", Simple.getTrans(R.string.subsystem_service_channeledit_info));
        Json.put(channeledit, "service", "dev");
        Json.put(channeledit, "feature", "usb+adb");
        Json.put(channeledit, "permission", "ext");

        Json.put(services, channeledit);

        JSONArray features = new JSONArray();
        Json.put(info, "features", features);

        JSONObject usbstick = new JSONObject();

        Json.put(usbstick, "name", Simple.getTrans(R.string.subsystem_feature_usbstick_name));
        Json.put(usbstick, "info", Simple.getTrans(R.string.subsystem_feature_usbstick_info));
        Json.put(usbstick, "feature", "usb");
        Json.put(usbstick, "permission", "ext");

        Json.put(features, usbstick);

        JSONObject adbaccess = new JSONObject();

        Json.put(adbaccess, "name", Simple.getTrans(R.string.subsystem_feature_adbaccess_name));
        Json.put(adbaccess, "info", Simple.getTrans(R.string.subsystem_feature_adbaccess_info));
        Json.put(adbaccess, "service", "dev");
        Json.put(adbaccess, "feature", "adb");

        Json.put(features, adbaccess);

        return info;
    }

    @Override
    public void startSubsystem()
    {
        SNYDiscover.startService();

        onSubsystemStarted("sny", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem()
    {
        SNYDiscover.stopService();

        onSubsystemStopped("sny", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
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