package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTRegister;

import zz.top.p2p.camera.P2PCloud;
import zz.top.tpl.base.TPLCloud;

public class ThirdParty
{
    private static final String LOGTAG = ThirdParty.class.getSimpleName();

    public static void initialize(Application context)
    {
        initialize_p2p(context);
        initialize_tpl(context);
    }

    private static void initialize_tpl(Application context)
    {
        tplCloud = new TPLCloud(context)
        {
            @Override
            public void onDeviceFound(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceFound:");

                IOTRegister.registerDevice(device);
            }
        };
    }

    private static TPLCloud tplCloud;
    private static P2PCloud p2pCloud;

    private static void initialize_p2p(Application context)
    {
        p2pCloud = new P2PCloud(context)
        {
            @Override
            public void onDeviceFound(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceFound:");

                IOTRegister.registerDevice(device);
            }
        };

        p2pCloud.login("dezi@kappa-mm.de", "blabla1234!");
    }
}
