package de.xavaro.android.brl.base;

import android.app.Application;

import org.json.JSONObject;

import pub.android.interfaces.ext.GetDeviceStatusRequest;
import pub.android.interfaces.ext.GetDevicesRequest;
import pub.android.interfaces.ext.GetSmartPlugHandler;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.pub.PUBSmartPlug;

import pub.android.stubs.OnInterfacesStubs;

import de.xavaro.android.brl.publics.SmartPlugHandler;
import de.xavaro.android.brl.comm.BRLDiscover;
import de.xavaro.android.brl.comm.BRLCommand;
import de.xavaro.android.brl.simple.Simple;
import de.xavaro.android.brl.simple.Json;
import de.xavaro.android.brl.R;

public class BRL extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDevicesRequest,
        GetSmartPlugHandler,
        GetDeviceStatusRequest
{
    private static final String LOGTAG = BRL.class.getSimpleName();

    public static BRL instance;

    public BRLDiscover discover;

    public BRL(Application application)
    {
        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        BRL.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "brl");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_brl_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_brl_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_broadlink_620));

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
        BRLDiscover.startService();

        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        BRLDiscover.stopService();

        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    //endregion SubSystemHandler

    //region GetSmartPlugHandler

    @Override
    public PUBSmartPlug getSmartPlugHandler(JSONObject device, JSONObject status, JSONObject credential)
    {
        String uuid = Json.getString(device, "uuid");
        String ipaddr = Json.getString(status, "ipaddr");
        String macaddr = Json.getString(device, "macaddr");

        return ((uuid != null) && (ipaddr != null) && (macaddr != null))
                ? new SmartPlugHandler(uuid, ipaddr, macaddr) : null;
    }

    //endregion GetSmartPlugHandler

    //region GetDeviceStatusRequest

    @Override
    public void discoverDevicesRequest()
    {
        BRLDiscover.startService();
    }

    @Override
    public boolean getDeviceStatusRequest(JSONObject device, final JSONObject status, JSONObject credential)
    {
        final String ipaddr = Json.getString(status, "ipaddr");
        final String macaddr = Json.getString(device, "macaddr");

        if ((ipaddr != null) && (macaddr != null))
        {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    int res = BRLCommand.getPowerStatus(ipaddr, macaddr);

                    if (res >= 0)
                    {
                        Json.put(status, "plugstate", res);

                        BRL.instance.onDeviceStatus(status);
                    }
                }
            };

            Simple.runBackground(runnable);

            return true;
        }

        return false;
    }

    //endregion GetDeviceStatusRequest
}
