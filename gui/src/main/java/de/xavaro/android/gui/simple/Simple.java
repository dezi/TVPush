package de.xavaro.android.gui.simple;

import android.Manifest;
import android.annotation.SuppressLint;

import android.app.Application;
import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Point;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Simple
{
    private static final String LOGTAG = Simple.class.getSimpleName();

    //region Basic defines.

    public static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    //endregion Basic defines.

    //region Device features.

    private static boolean istv;
    private static boolean issony;
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
    private static Resources resources;
    private static SharedPreferences prefs;
    private static ContentResolver contentResolver;

    private static WifiManager wifiManager;
    private static AudioManager audioManager;
    private static WindowManager windowManager;
    private static PackageManager packageManager;
    private static LocationManager locationManager;
    private static ConnectivityManager connectivityManager;
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter bluetoothAdapter;

    public static void initialize(Application app)
    {
        prefs = PreferenceManager.getDefaultSharedPreferences(app);
        handler = new Handler();
        resources = app.getResources();
        contentResolver = app.getContentResolver();

        packageManager = app.getPackageManager();
        wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
        audioManager = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);
        windowManager = ((WindowManager) app.getSystemService(Context.WINDOW_SERVICE));
        locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (windowManager != null)
        {
            Point size = new Point();
            windowManager.getDefaultDisplay().getRealSize(size);

            deviceWidth = size.x;
            deviceHeight = size.y;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            bluetoothManager = (BluetoothManager) app.getSystemService(Context.BLUETOOTH_SERVICE);

            if (bluetoothManager != null)
            {
                bluetoothAdapter = bluetoothManager.getAdapter();
            }
        }

        deviceDensity = Resources.getSystem().getDisplayMetrics().density;

        UiModeManager uiModeManager = (UiModeManager) app.getSystemService(Context.UI_MODE_SERVICE);
        istv = (uiModeManager != null) && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        issony = istv && getDeviceModelName().startsWith("BRAVIA");
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

    public static boolean isSony()
    {
        return issony;
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

    public static LocationManager getLocationManager()
    {
        return locationManager;
    }

    @Nullable
    public static BluetoothManager getBTManager()
    {
        return bluetoothManager;
    }

    @Nullable
    public static BluetoothAdapter getBTAdapter()
    {
        return bluetoothAdapter;
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

    public static int getRGBAlpha(int color)
    {
        return (color >> 24) & 0xff;
    }

    public static int setRGBAlpha(int color, int alpha)
    {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    public static void turnBeepOnOff(boolean on)
    {
        if ((audioManager != null) && !Simple.isTV())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, on
                        ? AudioManager.ADJUST_UNMUTE : AudioManager.ADJUST_MUTE, 0);
            }
            else
            {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, !on);
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

    public static void hideSoftKeyBoard(View view)
    {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static String getTrans(int resid, Object... args)
    {
        return String.format(resources.getString(resid), args);
    }

    public static String getRounded(double val)
    {
        DecimalFormat df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(val);
    }

    public static String getRounded(float val)
    {
        DecimalFormat df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(val);
    }

    //endregion Smart helpers.
}
