package de.xavaro.android.tvpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

public class EventsReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = EventsReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent)
    {
        //Log.d(LOGTAG, "onReceive: intent=" + intent);

        if (intent.getAction() == null) return;

        if (intent.getAction().equals("android.bluetooth.input.profile.action.MIC_INFO_RECEIVED"))
        {
            if (intent.getExtras() != null)
            {
                int state = intent.getExtras().getInt("android.bluetooth.BluetoothInputDevice.extra.MIC_STATE");

                //Log.d(LOGTAG, "onReceive: MIC_INFO_RECEIVED state=" + state);

                if (state == 1)
                {
                    //SystemsGUI.instance.displaySpeechRecognition(true);
                }
            }

            return;
        }

        //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
