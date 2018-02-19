package de.xavaro.android.common;

import android.app.UiModeManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static android.content.Context.UI_MODE_SERVICE;

public class Simple
{
    //region Device features.

    private static boolean istv;
    private static boolean istouch;
    private static boolean istablet;
    private static boolean iswidescreen;

    public static void checkFeatures(Context context)
    {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        istv = (uiModeManager != null) && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        PackageManager pmManager = context.getPackageManager();
        istouch = pmManager.hasSystemFeature("android.hardware.touchscreen");

        istablet = ((Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        int width = getDeviceWidth(context);
        int height = getDeviceHeight(context);
        iswidescreen = (width / (float) height) > (4 / 3f);
    }

    public static boolean isTV()
    {
        return istv;
    }

    public static boolean isTouch()
    {
        return istouch;
    }

    public static boolean isTablet()
    {
        return istablet;
    }

    public static boolean isWideScreen()
    {
        return iswidescreen;
    }

    public static boolean isOnline(Context context)
    {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        return (netInfo != null) && netInfo.isConnectedOrConnecting();
    }


    public static int getDeviceWidth(Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getDeviceHeight(Context context)
    {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.heightPixels;
    }

    //endregion
}
