package de.xavaro.android.gui.simple;

import android.annotation.SuppressLint;

import android.app.Application;
import android.app.UiModeManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Simple
{
    private static final String LOGTAG = Simple.class.getSimpleName();

    //region Basic defines.

    public static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    //endregion Basic defines.

    //region Device features.

    private static boolean istv;
    private static boolean istouch;
    private static boolean istablet;
    private static boolean iswidescreen;
    private static boolean isspeechin;
    private static boolean isretina;
    private static boolean iscamera;

    private static int deviceWidth;
    private static int deviceHeight;
    private static float deviceDensity;

    private static Handler handler;
    private static WifiManager wifiManager;
    private static WindowManager windowManager;
    private static PackageManager packageManager;
    private static ConnectivityManager connectivityManager;

    private static ContentResolver contentResolver;
    private static SharedPreferences prefs;

    public static void initialize(Application app)
    {
        handler = new Handler();

        prefs = PreferenceManager.getDefaultSharedPreferences(app);

        packageManager = app.getPackageManager();
        contentResolver = app.getContentResolver();

        wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
        windowManager = ((WindowManager) app.getSystemService(Context.WINDOW_SERVICE));
        connectivityManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (windowManager != null)
        {
            Point size = new Point();
            windowManager.getDefaultDisplay().getRealSize(size);

            deviceWidth = size.x;
            deviceHeight = size.y;
        }

        deviceDensity = Resources.getSystem().getDisplayMetrics().density;

        UiModeManager uiModeManager = (UiModeManager) app.getSystemService(Context.UI_MODE_SERVICE);
        istv = (uiModeManager != null) && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        iscamera = packageManager.hasSystemFeature("android.hardware.camera");
        istouch = packageManager.hasSystemFeature("android.hardware.touchscreen");

        istablet = ((Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        iswidescreen = (deviceWidth / (float) deviceHeight) > (4 / 3f);

        isspeechin = android.speech.SpeechRecognizer.isRecognitionAvailable(app);

        isretina = (deviceDensity >= 2.0);
    }

    public static boolean isTV()
    {
        return istv;
    }

    public static boolean isTouch()
    {
        return istouch;
    }

    public static boolean isPhone()
    {
        return ! istablet;
    }

    public static boolean isTablet()
    {
        return istablet;
    }

    public static boolean isWideScreen()
    {
        return iswidescreen;
    }

    public static boolean isRetina()
    {
        return isretina;
    }

    public static boolean isSpeechIn()
    {
        return isspeechin;
    }

    public static boolean isIscamera()
    {
        return iscamera;
    }

    public static boolean isOnline(Context context)
    {
        if (connectivityManager == null) return false;

        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();

        return (netInfo != null) && netInfo.isConnectedOrConnecting();
    }

    public static int getDeviceOrientation()
    {
        int orientation = Configuration.ORIENTATION_PORTRAIT;

        if (windowManager != null)
        {
            Point size = new Point();
            windowManager.getDefaultDisplay().getRealSize(size);

            if (size.x <= size.y)
            {
                orientation = Configuration.ORIENTATION_PORTRAIT;
            }
            else
            {
                orientation = Configuration.ORIENTATION_LANDSCAPE;
            }
        }

        return orientation;
    }

    public static int getDeviceWidth()
    {
        if (getDeviceOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
            return Math.min(deviceWidth, deviceHeight);
        }
        else
        {
            return Math.max(deviceWidth, deviceHeight);
        }
    }

    public static int getDeviceHeight()
    {
        if (getDeviceOrientation() == Configuration.ORIENTATION_PORTRAIT)
        {
            return Math.max(deviceWidth, deviceHeight);
        }
        else
        {
            return Math.min(deviceWidth, deviceHeight);
        }
    }

    public static float getDeviceDensity()
    {
        return deviceDensity;
    }

    //endregion Device features.

    //region Simple getters.

    public static Handler getHandler()
    {
        return handler;
    }

    public static String getConnectedWifiName()
    {
        String wifi = wifiManager.getConnectionInfo().getSSID();
        return wifi.replace("\"", "");
    }

    public static String getDeviceType()
    {
        if (isTV()) return "tv";
        if (isPhone()) return "phone";
        if (isTablet()) return "tablet";

        return "unknown";
    }

    public static String getDeviceCapabilities()
    {
        String caps = "";

        if (isTV())
        {
            caps += "tv|fixed|hd";

            if (getDeviceModelName().contains("BRAVIA 4K"))
            {
                caps += "|1080p|uhd|4k|mic";
            }
            else
            {
                if (getDeviceWidth() >= 1080)
                {
                    caps += "|1080p";
                }
                else
                {
                    caps += "|720p";
                }
            }
        }
        else
        {
            if (isTablet())
            {
                caps += "tablet|mic";
            }
            else
            {
                if (isPhone())
                {
                    caps += "phone|mic";
                }
                else
                {
                    caps += "unknown";
                }
            }
        }

        caps += "|speaker|tcp|wifi";

        if (isTouch()) caps += "|touch";
        if (isIscamera()) caps += "|camera";
        if (isWideScreen()) caps += "|widescreen";

        if (isSpeechIn() && caps.contains("|mic|")) caps += "|spechin";
        if (caps.contains("|speaker|")) caps += "|spechout";

        if ((getFCMToken() != null) && ! getFCMToken().isEmpty())
        {
            caps += "|fcm";
        }

        return caps;
    }

    public static String getDeviceUserName()
    {
        return Settings.Secure.getString(contentResolver, "bluetooth_name");
    }

    public static String getDeviceBrandName()
    {
        return Build.BRAND.toUpperCase();
    }

    public static String getDeviceModelName()
    {
        return Build.MODEL.toUpperCase();
    }

    public static String getDeviceFullName()
    {
        String brand = Build.BRAND.toUpperCase();
        String model = Build.MODEL.toUpperCase();

        if (model.startsWith(brand))
        {
            return model;
        }

        return brand + " " + model;
    }

    @SuppressLint("HardwareIds")
    public static String getDeviceId()
    {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID);
    }

    public static String getAndroidVersion()
    {
        return "Android " + Build.VERSION.RELEASE;
    }

    public static SharedPreferences getPrefs()
    {
        return prefs;
    }

    public static String getFCMToken()
    {
        return FirebaseInstanceId.getInstance().getToken();
    }

    //endregion Simple getters.

    //region View manipulation.

    public static void setSizeNODip(View view, int width, int height)
    {
        if (view.getLayoutParams() == null)
        {
            view.setLayoutParams(new ViewGroup.MarginLayoutParams(WC, WC));
        }

        view.getLayoutParams().width = width;
        view.getLayoutParams().height = height;
    }

    public static void setSizeDip(View view, int width, int height)
    {
        if (view.getLayoutParams() == null)
        {
            view.setLayoutParams(new ViewGroup.MarginLayoutParams(WC, WC));
        }

        view.getLayoutParams().width = width > 0 ? dipToPx(width) : width;
        view.getLayoutParams().height = height > 0 ? dipToPx(height) : height;
    }

    public static void setTextSizeDip(TextView textView, int size)
    {
        float real = size / textView.getContext().getResources().getConfiguration().fontScale;

        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, real);
    }

    public static void setPaddingDip(View view, int pad)
    {
        view.setPadding(dipToPx(pad), dipToPx(pad), dipToPx(pad), dipToPx(pad));
    }

    public static void setPaddingDip(View view, int left, int top, int right, int bottom)
    {
        view.setPadding(dipToPx(left), dipToPx(top), dipToPx(right), dipToPx(bottom));
    }

    public static void setMarginDip(View view, int margin)
    {
        if (view.getLayoutParams() == null)
            view.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));

        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dipToPx(margin);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = dipToPx(margin);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = dipToPx(margin);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin = dipToPx(margin);
    }

    public static void setMarginDip(View view, int left, int top, int right, int bottom)
    {
        if (view.getLayoutParams() == null)
            view.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));

        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = dipToPx(left);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).topMargin = dipToPx(top);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).rightMargin = dipToPx(right);
        ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).bottomMargin = dipToPx(bottom);
    }

    public static int getRGBAlpha(int color)
    {
        return (color >> 24) & 0xff;
    }

    public static int setRGBAlpha(int color, int alpha)
    {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    //endregion View manipulation.

    //region Smart helpers.

    public static int dipToPx(int dp)
    {
        return Math.round(dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float dipToPx(float dp)
    {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }

    public static int pxToDip(int px)
    {
        return Math.round(px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDip(float px)
    {
        return px / Resources.getSystem().getDisplayMetrics().density;
    }

    public static void turnBeepOnOff(Context context, boolean on)
    {
        if (! Simple.isTV())
        {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if (audioManager == null) return;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        on ? AudioManager.ADJUST_UNMUTE : AudioManager.ADJUST_MUTE, 0);
            }
            else
            {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, ! on);
            }
        }
    }

    public static Iterator<String> sortedIterator(Iterator<String> iterator)
    {
        ArrayList<String> list = new ArrayList<>();

        while (iterator.hasNext()) list.add(iterator.next());

        Collections.sort(list);

        return list.iterator();
    }

    @SuppressLint("ApplySharedPref")
    public static void removeALLPrefs()
    {
        prefs.edit().clear().commit();
    }

    @SuppressLint("ApplySharedPref")
    public static void removeALLPrefs(Context context)
    {
        PreferenceManager.getDefaultSharedPreferences(context).edit().clear().commit();
    }

    public static void dumpIntent(Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        for (String key : bundle.keySet())
        {
            Object value = bundle.get(key);

            Log.d(LOGTAG, "dumpIntent: key=" + key + " value=" + value);
        }
    }

    //endregion Smart helpers.
}