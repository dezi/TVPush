package de.xavaro.android.tvpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import de.xavaro.android.common.AppBase;

public class EventsReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = EventsReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOGTAG, "onReceive: intent=" + intent);

        if (intent.getAction() != null)
        {
            if (intent.getAction().equals("android.bluetooth.input.profile.action.MIC_INFO_RECEIVED"))
            {
                if (((AppBase) context.getApplicationContext()).getCurrentActivityClass() != SpeechActivity.class)
                {
                    Intent myIntent = new Intent(context, SpeechActivity.class);
                    context.startActivity(myIntent);
                }

                return;
            }
        }

        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
