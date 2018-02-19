package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

public class ApplicationBase extends Application
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        RegistrationService.startService(this);
    }
}
