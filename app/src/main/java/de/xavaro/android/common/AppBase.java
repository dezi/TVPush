package de.xavaro.android.common;

import android.annotation.SuppressLint;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

@SuppressLint("Registered")
public class AppBase extends Application implements Application.ActivityLifecycleCallbacks
{
    private static final String LOGTAG = AppBase.class.getSimpleName();

    private Class currentActivity;

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        Simple.checkFeatures(this);
    }

    @Override
    public void onActivityStopped(Activity activity)
    {
    }

    @Override
    public void onActivityStarted(Activity activity)
    {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState)
    {
    }

    @Override
    public void onActivityResumed(Activity activity)
    {
        currentActivity = activity.getClass();

        Log.d(LOGTAG, "onActivityResumed: activity=" + currentActivity.getSimpleName());
    }

    @Override
    public void onActivityPaused(Activity activity)
    {
        if (currentActivity == activity.getClass())
        {
            Log.d(LOGTAG, "onActivityPaused: activity=" + currentActivity.getSimpleName());

            currentActivity = null;
        }
    }

    @Override
    public void onActivityDestroyed(Activity activity)
    {
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState)
    {
    }

    @Nullable
    public Class getCurrentActivityClass()
    {
        return currentActivity;
    }
}
