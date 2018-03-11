package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTCloud;
import de.xavaro.android.iot.base.IOTRegister;

import zz.top.p2p.base.P2PCloud;
import zz.top.tpl.base.TPLCloud;

public class Systems
{
    private static final String LOGTAG = Systems.class.getSimpleName();

    private static SystemsGUI gui;
    private static IOT iot;
    private static TPLCloud tplCloud;
    private static P2PCloud p2pCloud;

    public static void initialize(Application appcontext)
    {
        initializeIOT(appcontext);
        initializeP2P(appcontext);
        initializeTPL(appcontext);

        gui = new SystemsGUI(appcontext);
    }

    private static void initializeIOT(Application appcontext)
    {
        iot = new IOT(appcontext)
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

    private static void initializeTPL(Application appcontext)
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

    private static void initializeP2P(Application appcontext)
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
