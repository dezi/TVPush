package de.xavaro.android.tvpush;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver
{
    private static final String LOGTAG = AutoStart.class.getSimpleName();

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOGTAG, "onReceive...");

        Intent serviceIntent = new Intent(context, RegistrationService.class);
        context.startService(serviceIntent);
    }
}