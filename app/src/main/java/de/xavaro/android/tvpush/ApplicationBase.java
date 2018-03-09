package de.xavaro.android.tvpush;

import de.xavaro.android.base.BaseApplication;
import de.xavaro.android.base.BaseRegistration;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.simple.Simple;

public class ApplicationBase extends BaseApplication
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        super.onCreate();

        Simple.initialize(this);

        //Simple.removeALLPrefs();

        IOT.initialize();

        ThirdPartyLogins.initialize();

        BaseRegistration.speechRecognitionActivityClass = SpeechRecognitionActivity.class;
    }
}
