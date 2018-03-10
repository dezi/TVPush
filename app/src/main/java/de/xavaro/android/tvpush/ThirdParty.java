package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTCloud;
import de.xavaro.android.iot.base.IOTRegister;

import zz.top.p2p.base.P2PCloud;
import zz.top.tpl.base.TPLCloud;

public class ThirdParty
{
    private static final String LOGTAG = ThirdParty.class.getSimpleName();

    private static IOTCloud iotCloud;
    private static TPLCloud tplCloud;
    private static P2PCloud p2pCloud;

    public static void initialize(Application appcontext)
    {
        initialize_iot(appcontext);
        initialize_p2p(appcontext);
        initialize_tpl(appcontext);
    }

    private static void initialize_iot(Application appcontext)
    {
        iotCloud = new IOTCloud(appcontext)
        {
            @Override
            public void onDeviceFound(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceFound:");

                IOTRegister.registerDevice(device);
            }

            @Override
            public void onDeviceAlive(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceAlive:");

                IOTRegister.registerDeviceAlive(device);
            }
        };
    }

    private static void initialize_tpl(Application appcontext)
    {
        tplCloud = new TPLCloud(appcontext)
        {
            @Override
            public void onDeviceFound(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceFound:");

                IOTRegister.registerDevice(device);
            }

            @Override
            public void onDeviceAlive(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceAlive:");

                IOTRegister.registerDeviceAlive(device);
            }
        };
    }

    private static void initialize_p2p(Application appcontext)
    {
        p2pCloud = new P2PCloud(appcontext)
        {
            @Override
            public void onDeviceFound(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceFound:");

                IOTRegister.registerDevice(device);
            }

            @Override
            public void onDeviceAlive(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceAlive:");

                IOTRegister.registerDeviceAlive(device);
            }
        };

        p2pCloud.login("dezi@kappa-mm.de", "blabla1234!");
    }
}
