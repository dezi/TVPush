package de.xavaro.android.tvpush;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessageService extends FirebaseMessagingService
{
    private static final String LOGTAG = FirebaseMessageService.class.getSimpleName();

    @Override
    public void onCreate()
    {
        //
        // The listener service is either started when the application
        // is started or by Android operating system, when a GCM message
        // is coming in (wake up).
        //

        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        RegistrationService.startService(this);
    }

    @Override
    public void onMessageReceived(RemoteMessage message)
    {
        Log.d(LOGTAG, "onMessageReceived...");
    }
}
