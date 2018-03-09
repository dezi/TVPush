package de.xavaro.android.tvpush;

import android.os.Handler;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.base.BaseApplication;
import de.xavaro.android.base.BaseRegistration;

import de.xavaro.android.iot.things.IOTRoot;
import zz.top.p2p.camera.P2PCloud;

public class ApplicationBase extends BaseApplication
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    public static final Handler handler = new Handler();

    public P2PCloud p2plogin;

    @Override
    public void onCreate()
    {
        super.onCreate();

        BaseRegistration.speechRecognitionActivityClass = SpeechRecognitionActivity.class;

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

        RegistrationService.startService(this);

        Log.d(LOGTAG, " IOTRoot.root=" + IOTRoot.root.uuid);
    }
}
