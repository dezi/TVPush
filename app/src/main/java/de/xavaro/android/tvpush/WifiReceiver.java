package de.xavaro.android.tvpush;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.util.Log;

import java.util.Iterator;
import java.util.Set;

public class WifiReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = WifiReceiver.class.getSimpleName();

    @Override
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOGTAG, "onReceive intent=" + intent.toString());

        dumpIntent(intent);

        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action))
        {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);

            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED)
            {
                Log.d(LOGTAG, "onReceive WIFI_P2P_STATE_ENABLED=true");
            }
            else
            {
                Log.d(LOGTAG, "onReceive WIFI_P2P_STATE_ENABLED=false");
            }
        }
    }

    public static void dumpIntent(Intent i)
    {
        Bundle bundle = i.getExtras();
        if (bundle != null)
        {
            Set<String> keys = bundle.keySet();
            Iterator<String> it = keys.iterator();

            Log.e(LOGTAG, "Dumping Intent start");

            while (it.hasNext())
            {
                String key = it.next();
                Log.e(LOGTAG, "[" + key + "=" + bundle.get(key) + "]");
            }

            Log.e(LOGTAG, "Dumping Intent end");
        }
    }
}


