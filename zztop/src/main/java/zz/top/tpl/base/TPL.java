package zz.top.tpl.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.comm.TPLMessageHandler;
import zz.top.tpl.comm.TPLMessageService;
import zz.top.tpl.handler.TPLHandlerSmartBulb;
import zz.top.tpl.handler.TPLHandlerSmartPlug;
import zz.top.tpl.handler.TPLHandlerSysInfo;
import zz.top.utl.Json;
import zz.top.utl.Simple;

import pub.android.interfaces.iot.InternetOfThingsHandler;

public class TPL implements InternetOfThingsHandler
{
    private static final String LOGTAG = TPL.class.getSimpleName();

    public static TPL instance;

    public TPLMessageHandler message;

    public TPL(Application application)
    {
        if (instance == null)
        {
            instance = this;

            Simple.initialize(application);

            TPLMessageHandler.initialize();
            TPLMessageService.startService();

            TPLHandlerSysInfo.sendAllGetSysinfo();
        }
        else
        {
            throw new RuntimeException("TPL system already initialized.");
        }
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

        String actioncmd = Json.getString(action, "action");
        String ipaddr = Json.getString(status, "ipaddr");

        if ((actioncmd != null) && (ipaddr != null))
        {
            if (actioncmd.equals("switchonled"))
            {
                TPLHandlerSmartPlug.sendLEDOnOff(ipaddr, true);
                return true;
            }

            if (actioncmd.equals("switchoffled"))
            {
                TPLHandlerSmartPlug.sendLEDOnOff(ipaddr, false);
                return true;
            }

            if (actioncmd.equals("switchonplug"))
            {
                TPLHandlerSmartPlug.sendPlugOnOff(ipaddr, true);
                return true;
            }

            if (actioncmd.equals("switchoffplug"))
            {
                TPLHandlerSmartPlug.sendPlugOnOff(ipaddr, false);
                return true;
            }

            if (actioncmd.equals("switchonbulb"))
            {
                TPLHandlerSmartBulb.sendBulbOnOff(ipaddr, true);
                return true;
            }

            if (actioncmd.equals("switchoffbulb"))
            {
                TPLHandlerSmartBulb.sendBulbOnOff(ipaddr, false);
                return true;
            }

            if (actioncmd.equals("adjustpos"))
            {
                int brightness = Json.getInt(status, "brightness");
                brightness += 25;

                TPLHandlerSmartBulb.sendBulbBrightness(ipaddr, brightness);
                return true;
            }

            if (actioncmd.equals("adjustneg"))
            {
                int brightness = Json.getInt(status, "brightness");
                brightness -= 25;

                TPLHandlerSmartBulb.sendBulbBrightness(ipaddr, brightness);
                return true;
            }
        }

        return false;
    }
}
