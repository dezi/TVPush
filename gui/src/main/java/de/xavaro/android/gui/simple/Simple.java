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
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.InputStream;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

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

    private static String packageName;
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

        packageName = app.getPackageName();
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

    public static boolean isDeveloper()
    {
        int devEnabled = Settings.Global.getInt(contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0);

        return (devEnabled == 1);
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

    public static int getDeviceWidthDip()
    {
        return pxToDip(getDeviceWidth());
    }

    public static int getDeviceHeightDip()
    {
        return pxToDip(getDeviceHeight());
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

    public static Resources getResources()
    {
        return resources;
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

    public static ContentResolver getContentResolver()
    {
        return contentResolver;
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

    public static int colorRGB(int hue, int saturation, int brightness)
    {
        float[] hsv = new float[3];

        hsv[ 0 ] = hue;
        hsv[ 1 ] = saturation / 100f;
        hsv[ 2 ] = brightness / 100f;

        return Color.HSVToColor(hsv);
    }

    public static int colorRGB(int rgbcolor, int brightness)
    {
        int r = (rgbcolor >> 16) & 0xff;
        int g = (rgbcolor >> 8) & 0xff;
        int b = rgbcolor& 0xff;

        float scale = 255 / (float) Math.max(r, Math.max(g, b));
        scale = scale * brightness / 100f;

        r = Math.round(r * scale);
        g = Math.round(g * scale);
        b = Math.round(b * scale);

        if (r > 255) r = 255;
        if (g > 255) g = 255;
        if (b > 255) b = 255;

        return ((r & 0xff) << 16) + ((g & 0xff) << 8) + (b & 0xff);
    }

    public static void colorHSV(String color)
    {
        int rgbcolor = Integer.parseInt(color, 16);
    }

    public static void colorHSV(int rgbcolor)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(rgbcolor, hsv);

        int hue = Math.round(hsv[0]);
        int saturation = Math.round(hsv[1] * 100);
        int brightness = Math.round(hsv[2] * 100);
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

    public static void showSoftKeyBoard(View view)
    {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(INPUT_METHOD_SERVICE);

        if (imm != null)
        {
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static String getTrans(int resid, Object... args)
    {
        return String.format(resources.getString(resid), args);
    }

    public static String getRounded3(double val)
    {
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(val);
    }

    public static String getRounded3(float val)
    {
        return getRounded3((double) val);
    }

    public static String getRounded6(double val)
    {
        DecimalFormat df = new DecimalFormat("#.######");
        df.setRoundingMode(RoundingMode.CEILING);

        return df.format(val);
    }

    public static String getRounded6(float val)
    {
        return getRounded6((double) val);
    }

    @Nullable
    public static InetAddress getInetAddress(String host)
    {
        try
        {
            return InetAddress.getByName(host);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    public static boolean getInetPing(InetAddress inetAddress, int timeout)
    {
        try
        {
            return inetAddress.isReachable(timeout);
        }
        catch (Exception ignore)
        {
        }

        return false;
    }

    @Nullable
    public static Boolean getMapBoolean(Map<String, Boolean> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    @Nullable
    public static Long getMapLong(Map<String, Long> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    @Nullable
    public static Integer getMapInteger(Map<String, Integer> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }


    @Nullable
    public static String getMapString(Map<String, String> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    @Nullable
    public static JSONObject getMapJSONObject(Map<String, JSONObject> map, String key)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return map.getOrDefault(key, null);
        }
        else
        {
            try
            {
                return map.get(key);
            }
            catch (Exception ignore)
            {
                return null;
            }
        }
    }

    public static void sleep(int time)
    {
        try
        {
            Thread.sleep(time);
        }
        catch (Exception ignore)
        {
        }
    }

    public static boolean isUIThread()
    {
        return (Looper.getMainLooper().getThread() == Thread.currentThread());
    }

    public static void setSystemProp(String prop, String level)
    {
        try
        {
            String command = "setprop " + prop + " " + level;

            Process setprop = Runtime.getRuntime().exec(command);
            int res = setprop.waitFor();

            Log.d(LOGTAG, "setSystemProp: res=" + res + " command=" + command);
        }
        catch (Exception ignore)
        {
        }
    }

    @Nullable
    public static String getManifestMetaData(String name)
    {
        try
        {
            ApplicationInfo ai = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            String value = ai.metaData.getString(name);

            Log.d(LOGTAG, "getManifestMetaData: name=" + name + " value=" + value);

            return value;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    public static String getImageResourceBase64(int resid)
    {
        try
        {
            InputStream is = resources.openRawResource(+resid);
            byte[] buffer = new byte[16 * 1024];
            int xfer = is.read(buffer);
            is.close();

            return Base64.encodeToString(buffer, 0 ,xfer, android.util.Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    //endregion Smart helpers.
}
