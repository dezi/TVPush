package de.xavaro.android.iot.base;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import de.xavaro.android.iot.comm.IOTMessaging;

public class IOTService extends Service
{
    private final static String LOGTAG = IOTService.class.getSimpleName();

    public static void startService(Context context)
    {
        Intent serviceIntent = new Intent(context, IOTService.class);
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

