package de.xavaro.android.tvpush;

import android.annotation.SuppressLint;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStartReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = AutoStartReceiver.class.getSimpleName();

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOGTAG, "onReceive...");
    }
}
