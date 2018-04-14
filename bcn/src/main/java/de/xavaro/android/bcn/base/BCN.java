package de.xavaro.android.bcn.base;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.all.SubSystemHandler;
import de.xavaro.android.pub.interfaces.ext.GetDevicesRequest;
import de.xavaro.android.pub.interfaces.ext.OnAliveHandler;
import de.xavaro.android.pub.interfaces.ext.OnDeviceHandler;
import de.xavaro.android.pub.interfaces.ext.OnLocationHandler;
import de.xavaro.android.pub.stubs.OnInterfacesStubs;

import de.xavaro.android.bcn.beacon.BCNScanner;
import de.xavaro.android.bcn.simple.Simple;
import de.xavaro.android.bcn.simple.Json;
import de.xavaro.android.bcn.R;

public class BCN extends OnInterfacesStubs implements
        SubSystemHandler,
        OnDeviceHandler,
        GetDevicesRequest,
        OnLocationHandler,
        OnAliveHandler
{
    private static final String LOGTAG = BCN.class.getSimpleName();

    public static BCN instance;

    public Application appcontext;
    public BCNScanner scanner;

    public BCN(Application application)
    {
        appcontext = application;

        Simple.initialize(application);
    }

    //region SubSystemHandler

    @Override
    public void setInstance()
    {
        BCN.instance = this;
    }
    
    @Override
    public JSONObject getSubsystemInfo()
    {
        int mode = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            ? SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY
            : SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE
            ;

        JSONObject info = new JSONObject();

        Json.put(info, "drv", "bcn");
        Json.put(info, "mode", mode);
        Json.put(info, "name", Simple.getTrans(R.string.subsystem_bcn_name));
        Json.put(info, "info", Simple.getTrans(R.string.subsystem_bcn_info));
        Json.put(info, "icon", Simple.getImageResourceBase64(R.drawable.subsystem_beacon_220));

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
        BCNScanner.startService(appcontext);

        onSubsystemStarted(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STARTED);
    }

    @Override
    public void stopSubsystem(String subsystem)
    {
        BCNScanner.stopService();

        onSubsystemStopped(subsystem, SubSystemHandler.SUBSYSTEM_RUN_STOPPED);
    }

    //endregion SubSystemHandler

    //region OnLocationHandler

    @Override
    public void onLocationMeasurement(JSONObject measurement)
    {
        Log.d(LOGTAG, "onLocationMeasurement: STUB!");
    }

    //endregion OnLocationHandler

    //region OnAliveHandler

    @Override
    public void onThingAlive(String uuid)
    {
        Log.d(LOGTAG, "onThingAlive: STUB!");
    }

    //endregion OnAliveHandler
}
