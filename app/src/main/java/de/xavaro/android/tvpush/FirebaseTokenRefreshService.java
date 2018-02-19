package de.xavaro.android.tvpush;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseTokenRefreshService extends FirebaseInstanceIdService
{
    private static final String LOGTAG = FirebaseTokenRefreshService.class.getSimpleName();

    @Override
    public void onTokenRefresh()
    {
        Log.d(LOGTAG, "onTokenRefresh...");
    }
}
