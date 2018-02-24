package de.xavaro.android.tvpush;

import android.app.Application;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;

import de.xavaro.android.common.Simple;
import de.xavaro.android.yihome.Barcode;
import de.xavaro.android.yihome.Camera;

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

        Camera.initialize();

        Log.d(LOGTAG, "onCreate: devicename=" + Simple.getDeviceUserName(this));
        Log.d(LOGTAG, "onCreate: fcmtoken=" + Simple.getFCMToken());

        Simple.checkFeatures(this);

        RegistrationService.startService(this);

        Log.d(LOGTAG, "#####" + Base64.encodeToString("1234abcd".getBytes(), 2));
        Log.d(LOGTAG, "#####" + new String(Base64.decode("RGV6aSBIb21l", 0)));
        Log.d(LOGTAG, "#####" + Barcode.EncodeBarcodeString(false, null));

        // EUnRsQDFHsENc3ks
        // EUQEawNvSq2gnY8j
        // EUITqsWMnX5XYuBr
    }
}
