package de.xavaro.android.tvpush;

import android.app.Application;
import android.os.Handler;
import android.util.Log;

import de.xavaro.android.common.Simple;

public class ApplicationBase extends Application
{
    private static final String LOGTAG = ApplicationBase.class.getSimpleName();

    public static final Handler handler = new Handler();

    static
    {
        System.loadLibrary("native-lib");
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        CameraTest.initialize();

        Log.d(LOGTAG, "onCreate: devicename=" + Simple.getDeviceUserName(this));
        Log.d(LOGTAG, "onCreate: fcmtoken=" + Simple.getFCMToken());

        Simple.checkFeatures(this);

        RegistrationService.startService(this);
    }
}
