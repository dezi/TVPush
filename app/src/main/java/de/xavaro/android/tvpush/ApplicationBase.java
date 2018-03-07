package de.xavaro.android.tvpush;

import android.os.Handler;

import de.xavaro.android.common.AppBase;

public class ApplicationBase extends AppBase
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    public static final Handler handler = new Handler();

    @Override
    public void onCreate()
    {
        super.onCreate();

        //RegistrationService.startService(this);
    }
}
