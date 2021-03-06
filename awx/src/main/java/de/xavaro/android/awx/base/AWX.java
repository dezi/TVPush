package de.xavaro.android.awx.base;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.awx.publics.SmartBulbHandler;
import de.xavaro.android.awx.simple.Log;
import de.xavaro.android.pub.interfaces.all.DoSomethingHandler;
import de.xavaro.android.pub.interfaces.ext.GetDeviceStatusRequest;
import de.xavaro.android.pub.interfaces.ext.GetDevicesRequest;
import de.xavaro.android.pub.interfaces.ext.GetSmartBulbHandler;
import de.xavaro.android.pub.interfaces.ext.OnDeviceHandler;
import de.xavaro.android.pub.interfaces.all.SubSystemHandler;

import de.xavaro.android.pub.interfaces.pub.PUBSmartBulb;
import de.xavaro.android.pub.stubs.OnInterfacesStubs;

import de.xavaro.android.awx.comm.AWXDiscover;
import de.xavaro.android.awx.simple.Simple;
import de.xavaro.android.awx.simple.Json;
import de.xavaro.android.awx.R;

public class AWX extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDevicesRequest,
        GetSmartBulbHandler,
        GetDeviceStatusRequest,
        DoSomethingHandler
{
    private static final String LOGTAG = AWX.class.getSimpleName();

    public static AWX instance;

    public AWXDiscover discover;
    public Application appcontext;

    public AWX(Application application)
    {
        appcontext = application;

        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        AWX.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "awx");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_awx_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_awx_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_awox_600));

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
        AWXDiscover.startService(appcontext);

        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        AWXDiscover.stopService();

        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    //endregion SubSystemHandler

    //region GetSmartBulbHandler

    @Override
    public PUBSmartBulb getSmartBulbHandler(JSONObject device, JSONObject status, JSONObject credential)
    {
        JSONObject credentials = Json.getObject(credential, "credentials");

        String uuid = Json.getString(device, "uuid");
        String did = Json.getString(device, "did");
        String meshname = Json.getString(credentials, "meshname");

        if ((uuid == null) || (did == null) || (meshname == null))
        {
            Log.e(LOGTAG, "getSmartBulbHandler: fail uuid=" + uuid + " did=" + did + " meshname=" + meshname);

            return null;
        }

        short meshid = (short) Integer.parseInt(did);

        return new SmartBulbHandler(uuid, meshname, meshid);
    }

    //endregion GetSmartBulbHandler

    //region GetDeviceStatusRequest

    @Override
    public void discoverDevicesRequest()
    {
        AWXDiscover.startService(appcontext);
    }

    @Override
    public boolean getDeviceStatusRequest(JSONObject device, JSONObject status, JSONObject credential)
    {
        if (Json.has(credential, "credentials"))
        {
            credential = Json.getObject(credential, "credentials");
        }

        String uuid = Json.getString(device, "uuid");
        String did = Json.getString(device, "did");
        String meshname = Json.getString(credential, "meshname");

        if ((uuid == null) || (did == null) || (meshname == null))
        {
            Log.e(LOGTAG, "getDeviceStatusRequest: fail uuid=" + uuid + " did=" + did + " meshname=" + meshname);

            return false;
        }

        short meshid = (short) Integer.parseInt(did);

        //new SmartBulbHandler(uuid, meshname, meshid).getState();

        return true;
    }

    //endregion GetDeviceStatusRequest

    //region DoSomethingHandler

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credential)
    {
        JSONObject credentials = Json.getObject(credential, "credentials");

        String uuid = Json.getString(device, "uuid");
        String did = Json.getString(device, "did");
        String meshname = Json.getString(credentials, "meshname");
        String actioncmd = Json.getString(action, "action");

        if ((actioncmd != null) && (uuid != null) && (did != null) && (meshname != null))
        {
            short meshid = (short) Integer.parseInt(did);

            if (actioncmd.equals("switchonbulb"))
            {
                new SmartBulbHandler(uuid, meshname, meshid).setBulbState(1);
                return true;
            }

            if (actioncmd.equals("switchoffbulb"))
            {
                new SmartBulbHandler(uuid, meshname, meshid).setBulbState(0);
                return true;
            }

            if (actioncmd.equals("adjustpos"))
            {
                int brightness = Json.getInt(status, "brightness");
                brightness += 50;

                new SmartBulbHandler(uuid, meshname, meshid).setBulbState(1);
                new SmartBulbHandler(uuid, meshname, meshid).setBulbBrightness(brightness);

                return true;
            }

            if (actioncmd.equals("adjustneg"))
            {
                int brightness = Json.getInt(status, "brightness");
                brightness -= 50;

                new SmartBulbHandler(uuid, meshname, meshid).setBulbState(1);
                new SmartBulbHandler(uuid, meshname, meshid).setBulbBrightness(brightness);

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

                        new SmartBulbHandler(uuid, meshname, meshid).setBulbState(1);
                        new SmartBulbHandler(uuid, meshname, meshid).setBulbRGB(rgbcolor);

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
