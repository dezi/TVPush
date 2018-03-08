package de.xavaro.android.tvpush;

import android.os.Handler;

import de.xavaro.android.base.BaseApplication;
import de.xavaro.android.base.BaseRegistration;
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
            protected void onLoginFailure(String message)
            {
                super.onLoginFailure(message);
            }

            @Override
            protected void onLoginSuccess()
            {
                super.onLoginSuccess();
            }
        };

        p2plogin.login("blabla1234!");

        RegistrationService.startService(this);
    }
}
