package de.xavaro.android.tvpush;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.os.IBinder;
import android.util.Log;

import de.xavaro.android.simple.Comm;

public class RegistrationService extends Service
{
    private final static String LOGTAG = RegistrationService.class.getSimpleName();

    private Thread worker = null;

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
        Toast.makeText(this, "RegistrationService started", Toast.LENGTH_SHORT).show();

        if (worker == null)
        {
            worker = new Comm(this);
            worker.start();
        }

        return START_NOT_STICKY; //START_STICKY;
    }

    @Override
    public void onDestroy()
    {
        Log.d(LOGTAG, "onDestroy...");

        try
        {
            if (worker != null)
            {
                worker.interrupt();
                worker.join();
                worker = null;
            }
        }
        catch (InterruptedException ignore)
        {
        }

        Toast.makeText(this, "RegistrationService Stopped", Toast.LENGTH_SHORT).show();
    }
}
