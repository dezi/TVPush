package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTRegister;

import zz.top.p2p.base.P2PCloud;

public class Systems
{
    private static final String LOGTAG = Systems.class.getSimpleName();

    private static SystemsGUI gui;
    private static SystemsIOT iot;
    private static SystemsTPL tpl;

    private static P2PCloud p2pCloud;

    public static void initialize(Application application)
    {
        iot = new SystemsIOT(application);
        tpl = new SystemsTPL(application);
        gui = new SystemsGUI(application);

        initializeP2P(application);
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
