package de.xavaro.android.tvpush;

import android.os.Handler;

import de.xavaro.android.base.BaseApplication;
import de.xavaro.android.base.BaseRegistration;

public class ApplicationBase extends BaseApplication
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    public static final Handler handler = new Handler();

    @Override
    public void onCreate()
    {
        super.onCreate();

        BaseRegistration.speechRecognitionActivity = SpeechRecognitionActivity.class;

        //RegistrationService.startService(this);
    }
}
