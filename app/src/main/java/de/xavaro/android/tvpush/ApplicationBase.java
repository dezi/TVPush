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

        p2pAppLogin("dezi@kappa-mm.de", "blabla1234!");

        RegistrationService.startService(this);
    }

    public P2PLogin p2plogin;

    private void p2pAppLogin(String email, String password)
    {
        p2plogin = new P2PLogin(email);

        p2plogin.login(password,
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d(LOGTAG, "onCreate: p2plogin success.");

                        p2pAppDeviceList();
                    }
                },
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d(LOGTAG, "onCreate: p2plogin failed.");
                    }
                });
    }

    private void p2pAppDeviceList()
    {
        p2plogin.deviceList(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d(LOGTAG, "onCreate: p2plist success.");
                    }
                },
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        Log.d(LOGTAG, "onCreate: p2plist failed.");
                    }
                }
        );
    }
}
