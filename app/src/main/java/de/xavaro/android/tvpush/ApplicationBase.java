package de.xavaro.android.tvpush;

import android.os.Handler;
import android.util.Log;

import de.xavaro.android.base.BaseApplication;
import de.xavaro.android.base.BaseRegistration;
import zz.top.p2p.camera.P2PLogin;

public class ApplicationBase extends BaseApplication
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    public static final Handler handler = new Handler();

    @Override
    public void onCreate()
    {
        super.onCreate();

        BaseRegistration.speechRecognitionActivityClass = SpeechRecognitionActivity.class;

        P2PLogin.login("dezi@kappa-mm.de", "blabla1234!");

        //RegistrationService.startService(this);
    }
}
