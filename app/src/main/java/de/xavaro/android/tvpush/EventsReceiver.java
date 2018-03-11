package de.xavaro.android.tvpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

import de.xavaro.android.gui.simple.Simple;

public class EventsReceiver extends BroadcastReceiver
{
    private static final String LOGTAG = EventsReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent)
    {
        Log.d(LOGTAG, "onReceive: intent=" + intent);
        if (intent.getAction() == null) return;

        if (intent.getAction().equals("android.bluetooth.input.profile.action.MIC_INFO_RECEIVED"))
        {
            Simple.dumpIntent(intent);

            if (intent.getExtras() != null)
            {
                int state = intent.getExtras().getInt("android.bluetooth.BluetoothInputDevice.extra.MIC_STATE");

                if (state == 1)
                {
                    SystemsGUI.instance.displaySpeechRecognition();
                }
            }

            return;
        }

        Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
    }
}
