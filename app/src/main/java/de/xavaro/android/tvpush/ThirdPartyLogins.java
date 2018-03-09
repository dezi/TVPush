package de.xavaro.android.tvpush;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTRegister;
import zz.top.p2p.camera.P2PCloud;

public class ThirdPartyLogins
{
    public static void initialize()
    {
        initialize_p2p();
    }

    private static P2PCloud p2plogin;

    public static void initialize_p2p()
    {
        p2plogin = new P2PCloud("dezi@kappa-mm.de")
        {
            @Override
            protected void onDeviceFound(JSONObject device)
            {
                Log.d(LOGTAG, "onDeviceFound:");

                IOTRegister.registerDevice(device);
            }
        };

        p2plogin.login("blabla1234!");
    }
}
