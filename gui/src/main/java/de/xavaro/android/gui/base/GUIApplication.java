package de.xavaro.android.gui.base;

import android.annotation.SuppressLint;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import android.app.Application;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.smart.GUISpeechListener;

@SuppressLint("Registered")
public class GUIApplication extends Application implements Application.ActivityLifecycleCallbacks
{
    private static final String LOGTAG = GUIApplication.class.getSimpleName();

    public Activity currentActivity;

    @Override
    public void onCreate()
    {
        Log.d(LOGTAG, "onCreate...");

        super.onCreate();

        registerActivityLifecycleCallbacks(this);

        Simple.initialize(this);

        Log.d(LOGTAG, "onCreate:"
                + " model=" + Simple.getDeviceFullName()
                + " width=" + Simple.getDeviceWidth()
                + " height=" + Simple.getDeviceHeight()
                + " density=" + Simple.getDeviceDensity()
        );

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
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
        return ((GUIApplication) context.getApplicationContext()).currentActivity;
    }

    @Nullable
    public static Class getCurrentActivityClass(Context context)
    {
        Activity current = getCurrentActivity(context);
        return (current != null) ? current.getClass() : null;
    }

    @Nullable
    public static void setKeepScreenOnOff(Context context, boolean keepon)
    {
        Activity current = getCurrentActivity(context);

        if (current != null)
        {
            if (keepon)
            {
                current.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
            else
            {
                current.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            }
        }
    }
}
