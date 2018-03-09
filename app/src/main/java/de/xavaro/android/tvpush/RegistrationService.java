package de.xavaro.android.tvpush;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.IBinder;
import android.util.Log;

import de.xavaro.android.iot.comm.IOTMessaging;
import de.xavaro.android.simple.Comm;

public class RegistrationService extends Service
{
    private final static String LOGTAG = RegistrationService.class.getSimpleName();

    public static void startService(Context context)
    {
        Intent serviceIntent = new Intent(context, RegistrationService.class);
        context.startService(serviceIntent);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        Log.d(LOGTAG, "onBind...");

        return null;
    }

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d(LOGTAG, "onStartCommand.");

        IOTMessaging.startService();

        return START_NOT_STICKY; //START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy.");

        IOTMessaging.stopService();
    }
}
