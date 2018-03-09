package de.xavaro.android.tvpush;

import android.util.Log;

import org.json.JSONObject;

import zz.top.p2p.camera.P2PCloud;

public class ThirdPartyLogins
{
    public static P2PCloud p2plogin;

    public static void initialize()
    {
        p2plogin = new P2PCloud("dezi@kappa-mm.de")
        {
            @Override
            protected void onRestApiFailure(String message, String what, JSONObject params, JSONObject result)
            {
                Log.d(LOGTAG, message);
            }

            @Override
            protected void onLoginSuccess(String what, JSONObject params, JSONObject result)
            {
                Log.d(LOGTAG, "Login success.");
            }

            @Override
            protected void onListSuccess(String what, JSONObject params, JSONObject result)
            {
                Log.d(LOGTAG, "List success.");
            }
        };

        p2plogin.login("blabla1234!");
    }
}
