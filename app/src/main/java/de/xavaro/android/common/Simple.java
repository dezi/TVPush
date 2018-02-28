package de.xavaro.android.common;

import android.app.UiModeManager;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.Context;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.ViewGroup;

import com.google.firebase.iid.FirebaseInstanceId;

public class Simple
{
    //region Device features.

    private static boolean istv;
    private static boolean istouch;
    private static boolean istablet;
    private static boolean iswidescreen;
    private static boolean isspeech;

    public static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    public static void checkFeatures(Context context)
    {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
        istv = (uiModeManager != null) && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        PackageManager pmManager = context.getPackageManager();
        istouch = pmManager.hasSystemFeature("android.hardware.touchscreen");

        istablet = ((Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        int width = getDeviceWidth(context);
        int height = getDeviceHeight(context);
        iswidescreen = (width / (float) height) > (4 / 3f);

        isspeech = android.speech.SpeechRecognizer.isRecognitionAvailable(context);
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

    public static boolean isIsspeech()
    {
        return isspeech;
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

    public static String getDeviceUserName(Context context)
    {
        return Settings.Secure.getString(context.getContentResolver(), "bluetooth_name");
    }

    public static String getFCMToken()
    {
        return FirebaseInstanceId.getInstance().getToken();
    }

    public static String getHexBytesToString(byte[] bytes, int offset, int length)
    {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[ length << 1 ];

        for (int inx = offset; inx < (length + offset); inx++)
        {
            //noinspection PointlessArithmeticExpression
            hexChars[ ((inx - offset) << 1) + 0 ] = hexArray[ (bytes[ inx ] >> 4) & 0x0f ];
            //noinspection PointlessBitwiseExpression
            hexChars[ ((inx - offset) << 1) + 1 ] = hexArray[ (bytes[ inx ] >> 0) & 0x0f ];
        }

        return String.valueOf(hexChars);
    }

}
