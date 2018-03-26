package zz.top.tpl.base;

import android.app.Application;
import android.graphics.Color;

import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.drv.SmartBulb;
import pub.android.interfaces.drv.SmartPlug;
import pub.android.interfaces.ext.GetSmartBulbHandler;
import pub.android.interfaces.ext.GetSmartPlugHandler;

import zz.top.tpl.comm.TPLMessageHandler;
import zz.top.tpl.comm.TPLMessageService;
import zz.top.tpl.comm.TPLUDP;
import zz.top.tpl.handler.TPLHandlerSmartBulb;
import zz.top.tpl.handler.TPLHandlerSmartPlug;
import zz.top.tpl.handler.TPLHandlerSysInfo;
import zz.top.tpl.publics.SmartBulbHandler;
import zz.top.tpl.publics.SmartPlugHandler;
import zz.top.utl.Log;
import zz.top.utl.Simple;
import zz.top.utl.Json;

import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.ext.PutStatusRequest;
import pub.android.interfaces.all.DoSomethingHandler;

public class TPL implements
        SubSystemHandler,
        OnDeviceHandler,
        PutStatusRequest,
        DoSomethingHandler,
        GetSmartPlugHandler,
        GetSmartBulbHandler
{
    private static final String LOGTAG = TPL.class.getSimpleName();

    public static TPL instance;

    public TPLMessageHandler message;
    public TPLMessageService receiver;

    public TPL(Application application)
    {
        Simple.initialize(application);
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "tpl");
        Json.put(info, "name", "TP-Link Smart Home");

        return info;
    }

    @Override
    public void startSubsystem()
    {
        TPLMessageHandler.startService();
        TPLMessageService.startService();
        TPLUDP.startService();

        TPLHandlerSysInfo.sendAllGetSysinfo();

        onSubsystemStarted("tpl", SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem()
    {
        TPLUDP.stopService();
        TPLMessageService.stopService();
        TPLMessageHandler.stopService();

        onSubsystemStopped("tpl", SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
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
    public boolean putDeviceStatusRequest(JSONObject iotDevice)
    {
        TPLHandlerSysInfo.sendAllGetSysinfo();

        return true;
    }

    @Override
    public SmartPlug getSmartPlugHandler(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String ipaddr = Json.getString(status, "ipaddr");
        return (ipaddr != null) ? new SmartPlugHandler(ipaddr) : null;
    }

    @Override
    public SmartBulb getSmartBulbHandler(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String ipaddr = Json.getString(status, "ipaddr");
        return (ipaddr != null) ? new SmartBulbHandler(ipaddr) : null;
    }

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        //Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));
        //Log.d(LOGTAG, "doSomething: status=" + Json.toPretty(status));

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
                brightness += 50;

                TPLHandlerSmartBulb.sendBulbOnOff(ipaddr, true);
                TPLHandlerSmartBulb.sendBulbBrightness(ipaddr, brightness);
                return true;
            }

            if (actioncmd.equals("adjustneg"))
            {
                int brightness = Json.getInt(status, "brightness");
                brightness -= 50;

                TPLHandlerSmartBulb.sendBulbOnOff(ipaddr, true);
                TPLHandlerSmartBulb.sendBulbBrightness(ipaddr, brightness);
                return true;
            }

            if (actioncmd.equals("color"))
            {
                String color = Json.getString(action, "actionData");

                if (color != null)
                {
                    try
                    {
                        int rgbcolor = Integer.parseInt(color, 16);

                        float[] hsv = new float[3];
                        Color.colorToHSV(rgbcolor, hsv);

                        int hue = Math.round(hsv[0]);
                        int saturation = Math.round(hsv[1] * 100);
                        int brightness = Math.round(hsv[2] * 100);

                        if ((rgbcolor == 0xffffff)
                                || (rgbcolor == 0x888888)
                                || (rgbcolor == 0x777777)
                                || (rgbcolor == 0x666666)
                                || (rgbcolor == 0x555555)
                                || (rgbcolor == 0x444444)
                                || (rgbcolor == 0x333333)
                                || (rgbcolor == 0x222222)
                                || (rgbcolor == 0x111111)
                                || (rgbcolor == 0x000000))
                        {
                            //
                            // Dimm intention.
                            //

                            TPLHandlerSmartBulb.sendBulbOnOff(ipaddr, true);
                            TPLHandlerSmartBulb.sendBulbBrightness(ipaddr, brightness);
                        }
                        else
                        {
                            //
                            // Color intention. Leave brightness untouched
                            //

                            TPLHandlerSmartBulb.sendBulbOnOff(ipaddr, true);
                            TPLHandlerSmartBulb.sendBulbHSOnly(ipaddr, hue, saturation);
                        }

                        return true;
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        }

        return false;
    }
}
