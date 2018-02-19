package de.xavaro.android.tvpush;

import android.app.Service;
import android.content.Intent;
import android.widget.Toast;
import android.os.IBinder;
import android.util.Log;

public class RegistrationService extends Service
{
    private final static String LOGTAG = RegistrationService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(LOGTAG, "onBind...");

        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate");

        Toast.makeText(this, "RegistrationService created", Toast.LENGTH_LONG).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        return START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy...");

        Toast.makeText(this, "RegistrationService Stopped", Toast.LENGTH_LONG).show();
    }
}
