package de.xavaro.android.tvpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import de.xavaro.android.base.BaseApplication;

import de.xavaro.android.base.BaseRegistration;
import de.xavaro.android.simple.Dump;

public class EventsReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = EventsReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOGTAG, "onReceive: intent=" + intent);
        if (intent.getAction() == null) return;

        if (intent.getAction().equals("android.bluetooth.input.profile.action.MIC_INFO_RECEIVED"))
        {
            Dump.dumpIntent(intent);

            if (intent.getExtras() != null)
            {
                int state = intent.getExtras().getInt("android.bluetooth.BluetoothInputDevice.extra.MIC_STATE");

                if ((state == 1) && (BaseRegistration.speechRecognitionActivityClass != null))
                {
                    if (BaseApplication.getCurrentActivityClass(context) != BaseRegistration.speechRecognitionActivityClass)
                    {
                        Intent myIntent = new Intent(context, SpeechRecognitionActivity.class);
                        //context.startActivity(myIntent);
                    }
                }
            }

            return;
        }

        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
