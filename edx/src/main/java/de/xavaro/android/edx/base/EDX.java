package de.xavaro.android.edx.base;


import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.edx.publics.SmartPlugHandler;
import de.xavaro.android.edx.simple.Log;
import pub.android.interfaces.ext.GetDeviceCredentials;
import pub.android.interfaces.ext.GetDevicesRequest;
import pub.android.interfaces.ext.GetSmartPlugHandler;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.pub.PUBSmartPlug;
import pub.android.stubs.OnInterfacesStubs;

import de.xavaro.android.edx.comm.EDXDiscover;

import de.xavaro.android.edx.simple.Simple;
import de.xavaro.android.edx.simple.Json;
import de.xavaro.android.edx.R;

public class EDX extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDevicesRequest,
        GetSmartPlugHandler
{
    private static final String LOGTAG = EDX.class.getSimpleName();

    public static EDX instance;

    public EDXDiscover discover;

    public EDX(Application application)
    {
        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        EDX.instance = this;
    }

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "edx");
        Json.put(info, "mode", SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_edx_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_edx_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_edimax_550));

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
        EDXDiscover.startService();

        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        EDXDiscover.stopService();

        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    //endregion SubSystemHandler

    //region GetDevicesRequest

    @Override
    public JSONObject onGetDeviceRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetDeviceRequest: STUB!");

        return null;
    }

    @Override
    public JSONObject onGetStatusRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetStatusRequest: STUB!");

        return null;
    }

    @Override
    public JSONObject onGetCredentialRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetCredentialRequest: STUB!");

        return null;
    }

    @Override
    public JSONObject onGetMetaRequest(String uuid)
    {
        Log.d(LOGTAG, "onGetMetaRequest: STUB!");

        return null;
    }

    @Override
    public JSONArray onGetDevicesCapabilityRequest(String capability)
    {
        Log.d(LOGTAG, "onGetDevicesCapabilityRequest: STUB!");

        return null;
    }

    //endregion GetDevicesRequest

    @Override
    public PUBSmartPlug getSmartPlugHandler(JSONObject device, JSONObject status, JSONObject credential)
    {
        String uuid = Json.getString(device, "uuid");

        String ipaddr = Json.getString(status, "ipaddr");
        int ipport = Json.getInt(status, "ipport");

        JSONObject credentials = Json.getObject(credential, "credentials");

        String user = Json.getString(credentials, "localUser");
        String pass = Json.getString(credentials, "localPass");

        return ((uuid != null) && (ipaddr != null) && (ipport !=0) && (user != null) && (pass != null))
                ? new SmartPlugHandler(uuid, ipaddr, ipport, user, pass) : null;
    }
}
