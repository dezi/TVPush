package de.xavaro.android.base;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import de.xavaro.android.simple.Simple;

@SuppressLint("Registered")
public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks
{
    private static final String LOGTAG = BaseApplication.class.getSimpleName();

    private Activity currentActivity;

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        Simple.checkFeatures(this);

        Log.d(LOGTAG, "onCreate:"
                + " model=" + Simple.getDeviceModelName()
                + " width=" + Simple.getDeviceWidth()
                + " height=" + Simple.getDeviceHeight()
                + " density=" + Simple.getDeviceDensity()
        );
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
        currentActivity = activity;

        Log.d(LOGTAG, "onActivityResumed: activity=" + currentActivity.getClass().getSimpleName());
    }

    @Override
    public void onActivityPaused(Activity activity)
    {
        if (currentActivity == activity)
        {
            Log.d(LOGTAG, "onActivityPaused: activity=" + currentActivity.getClass().getSimpleName());

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
    public static Activity getCurrentActivity(Context context)
    {
        return ((BaseApplication) context.getApplicationContext()).currentActivity;
    }

    @Nullable
    public static Class getCurrentActivityClass(Context context)
    {
        Activity current = getCurrentActivity(context);
        return (current != null) ? current.getClass() : null;
    }
}
