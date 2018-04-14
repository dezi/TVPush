package de.xavaro.android.tpl.base;

import android.app.Application;
import android.graphics.Color;

import org.json.JSONObject;

import pub.android.interfaces.ext.GetSmartBulbHandler;
import pub.android.interfaces.ext.GetSmartPlugHandler;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.ext.GetDeviceStatusRequest;
import pub.android.interfaces.all.DoSomethingHandler;
import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.pub.PUBSmartBulb;
import pub.android.interfaces.pub.PUBSmartPlug;
import pub.android.stubs.OnInterfacesStubs;

import de.xavaro.android.tpl.publics.SmartBulbHandler;
import de.xavaro.android.tpl.publics.SmartPlugHandler;
import de.xavaro.android.tpl.handler.TPLHandlerSysInfo;
import de.xavaro.android.tpl.handler.TPLHandlerSmartBulb;
import de.xavaro.android.tpl.handler.TPLHandlerSmartPlug;
import de.xavaro.android.tpl.comm.TPLDiscover;
import de.xavaro.android.tpl.simple.Simple;
import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.R;

public class TPL extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDeviceStatusRequest,
        GetSmartPlugHandler,
        GetSmartBulbHandler,
        DoSomethingHandler
{
    public static TPL instance;

    public TPLDiscover discover;

    public TPL(Application application)
    {
        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        TPL.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "tpl");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_tpl_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_tpl_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_tp_link_410));

        return info;
    }

    @Override
    public JSONObject getSubsystemSettings()
    {
        return getSubsystemInfo();
    }

    @Override
    public void startSubsystem(String subsystem)
    {
        TPLDiscover.startService();

        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        TPLDiscover.stopService();

        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    //endregion SubSystemHandler

    //region GetSmartPlugHandler

    @Override
    public PUBSmartPlug getSmartPlugHandler(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String ipaddr = Json.getString(status, "ipaddr");

        return ((uuid != null) && (ipaddr != null)) ? new SmartPlugHandler(uuid, ipaddr) : null;
    }

    //endregion GetSmartPlugHandler

    //region GetSmartBulbHandler

    @Override
    public PUBSmartBulb getSmartBulbHandler(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String ipaddr = Json.getString(status, "ipaddr");

        return (ipaddr != null) ? new SmartBulbHandler(uuid, ipaddr) : null;
    }

    //endregion GetSmartBulbHandler

    //region GetDeviceStatusRequest

    @Override
    public void discoverDevicesRequest()
    {
        TPLDiscover.startService();
    }

    @Override
    public boolean getDeviceStatusRequest(JSONObject device, JSONObject status, JSONObject credential)
    {
        final String ipaddr = Json.getString(status, "ipaddr");
        final int ipport = Json.getInt(status, "ipport");

        if ((ipaddr != null) && (ipport != 0))
        {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    String result = TPLHandlerSysInfo.sendSysInfo(ipaddr);
                    JSONObject message = Json.fromStringObject(result);

                    if (message != null)
                    {
                        TPLHandlerSysInfo.buildDeviceDescription(ipaddr, ipport, message, true);
                    }
                }
            };

            Simple.runBackground(runnable);

            return true;
        }

        return false;
    }

    //endregion GetDeviceStatusRequest

    //region DoSomethingHandler

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String ipaddr = Json.getString(status, "ipaddr");
        String actioncmd = Json.getString(action, "action");

        if ((actioncmd != null) && (ipaddr != null))
        {
            if (actioncmd.equals("switchonled"))
            {
                new SmartPlugHandler(uuid, ipaddr).setLEDState(1);
                return true;
            }

            if (actioncmd.equals("switchoffled"))
            {
                new SmartPlugHandler(uuid, ipaddr).setLEDState(0);
                return true;
            }

            if (actioncmd.equals("switchonplug"))
            {
                new SmartPlugHandler(uuid, ipaddr).setPlugState(1);
                return true;
            }

            if (actioncmd.equals("switchoffplug"))
            {
                new SmartPlugHandler(uuid, ipaddr).setPlugState(0);
                return true;
            }

            if (actioncmd.equals("switchonbulb"))
            {
                new SmartBulbHandler(uuid, ipaddr).setBulbState(1);
                return true;
            }

            if (actioncmd.equals("switchoffbulb"))
            {
                new SmartBulbHandler(uuid, ipaddr).setBulbState(0);
                return true;
            }

            if (actioncmd.equals("adjustpos"))
            {
                int brightness = Json.getInt(status, "brightness");
                brightness += 50;

                new SmartBulbHandler(uuid, ipaddr).setBulbState(1);
                new SmartBulbHandler(uuid, ipaddr).setBulbBrightness(brightness);

                return true;
            }

            if (actioncmd.equals("adjustneg"))
            {
                int brightness = Json.getInt(status, "brightness");
                brightness -= 50;

                new SmartBulbHandler(uuid, ipaddr).setBulbState(1);
                new SmartBulbHandler(uuid, ipaddr).setBulbBrightness(brightness);

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

                            new SmartBulbHandler(uuid, ipaddr).setBulbState(1);
                            new SmartBulbHandler(uuid, ipaddr).setBulbBrightness(brightness);
                        }
                        else
                        {
                            //
                            // Color intention. Leave brightness untouched.
                            //

                            new SmartBulbHandler(uuid, ipaddr).setBulbState(1);
                            new SmartBulbHandler(uuid, ipaddr).setBulbHSOnly(hue, saturation);
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

    //endregion DoSomethingHandler
}
