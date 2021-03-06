package de.xavaro.android.iot.simple;

import android.Manifest;
import android.annotation.SuppressLint;
import android.location.LocationManager;
import android.support.annotation.Nullable;

import android.app.Application;
import android.app.UiModeManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.Context;
import android.graphics.Point;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.text.format.Formatter;
import android.util.Base64;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.os.Handler;
import android.os.Build;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

public class Simple
{
    private static final String LOGTAG = Simple.class.getSimpleName();

    //region Basic defines.

    public static final int MP = ViewGroup.LayoutParams.MATCH_PARENT;
    public static final int WC = ViewGroup.LayoutParams.WRAP_CONTENT;

    //endregion Basic defines.

    //region Device features.

    private static boolean istv;
    private static boolean isgps;
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
    private static WindowManager windowManager;
    private static PackageManager packageManager;
    private static LocationManager locationManager;
    private static ConnectivityManager connectivityManager;
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter bluetoothAdapter;

    public static void initialize(Application app)
    {
        handler = new Handler();
        prefs = PreferenceManager.getDefaultSharedPreferences(app);
        resources = app.getResources();
        packageManager = app.getPackageManager();
        contentResolver = app.getContentResolver();

        wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
        windowManager = ((WindowManager) app.getSystemService(Context.WINDOW_SERVICE));
        locationManager = (LocationManager) app.getSystemService(Context.LOCATION_SERVICE);
        connectivityManager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2)
        {
            bluetoothManager = (BluetoothManager) app.getSystemService(Context.BLUETOOTH_SERVICE);

            if (bluetoothManager != null)
            {
                bluetoothAdapter = bluetoothManager.getAdapter();
            }
        }

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

        issony = istv && getDeviceModelName().startsWith("BRAVIA");
        iscamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        istouch = packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
        isgps = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        istablet = ((Resources.getSystem().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE);

        iswidescreen = (deviceWidth / (float) deviceHeight) > (4 / 3f);

        isspeechin = SpeechRecognizer.isRecognitionAvailable(app);

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

    public static boolean isGps()
    {
        return isgps;
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

    @Nullable
    @SuppressWarnings("deprecation")
    public static String getConnectedWifiIPAddress()
    {
        if (wifiManager == null) return null;

        int ipint = wifiManager.getConnectionInfo().getIpAddress();
        return Formatter.formatIpAddress(ipint);
    }

    public static String getDeviceType()
    {
        if (isTV()) return "tv";
        if (isPhone()) return "phone";
        if (isTablet()) return "tablet";

        return "unknown";
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

    static public boolean checkBTFeatures(Context context)
    {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE))
        {
            Log.e(LOGTAG, "checkBTFeatures: Bluetooth LE is not supported!");

            return false;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            Log.d(LOGTAG, "checkBTFeatures: Android version too old!");

            return false;
        }

        BluetoothAdapter ba = Simple.getBTAdapter();

        if (ba == null)
        {
            Log.e(LOGTAG, "checkBTFeatures: BluetoothAdapter ist not available!");

            return false;
        }

        if (!ba.isEnabled()) ba.enable();

        if (ba.isEnabled())
        {
            Log.d(LOGTAG, "checkBTFeatures: BluetoothAdapter is enabled!");
        }
        else
        {
            //Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            //context.startActivityForResult(enableIntent, REQUEST_ENABLE_BT);

            Log.e(LOGTAG, "checkBTFeatures: BluetoothAdapter is not enabled!");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            if (ba.isMultipleAdvertisementSupported())
            {
                Log.d(LOGTAG, "checkBTFeatures: Has Multiple Advertisement Support!");
            }
            else
            {
                Log.e(LOGTAG, "checkBTFeatures: No Multiple Advertisement Support!");
            }
        }

        return ba.isEnabled();
    }

    public static String getString(byte[] bytes, int offset, int size)
    {
        try
        {
            return new String(bytes, offset, size, "UTF-8");
        }
        catch (Exception ignore)
        {
        }

        return new String(bytes, offset, size);
    }

    public static LocationManager getLocationManager()
    {
        return locationManager;
    }

    public static PackageManager getPackageManager()
    {
        return packageManager;
    }

    public static boolean checkLocationPermission(Context context)
    {
        boolean coarse = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        boolean fine = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;

        Log.d(LOGTAG, "checkLocationPermission: fine=" + fine + " coarse=" + coarse);

        return coarse && fine;
    }

    public static boolean checkStoragePermission(Context context)
    {
        boolean read = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        boolean write = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;

        Log.d(LOGTAG, "checkStoragePermission: read=" + read + " write=" + write);

        return read && write;
    }

    public static String padZero(int number, int digits)
    {
        String str = Integer.valueOf(number).toString();

        while (str.length() < digits) str = "0" + str;

        return str;
    }

    public static long getAgeInSeconds(long timestamp)
    {
        return (System.currentTimeMillis() - timestamp) / 1000;
    }

    public static boolean writeTextFile(File filename, String content)
    {
        return writeBinaryFile(filename, content.getBytes());
    }

    public static boolean writeBinaryFile(File filename, byte[] bytes)
    {
        try
        {
            FileOutputStream outputStream = new FileOutputStream(filename);
            outputStream.write(bytes);
            outputStream.close();

            return true;
        }
        catch (FileNotFoundException ignore)
        {
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return false;
    }

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

    public static ArrayList<Runnable> getMapRunnables(Map<String, ArrayList<Runnable>> map, String key)
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

    public static String padLeft(String str, int pad)
    {
        while (str.length() < pad) str = " " + str;

        return str;
    }

    public static String padRight(String str, int pad)
    {
        while (str.length() < pad) str = str + " ";

        return str;
    }

    public static String padLeft(int val, int pad)
    {
        return padLeft(Integer.valueOf(val).toString(), pad);
    }

    public static String padLeft(long val, int pad)
    {
        return padLeft(Long.valueOf(val).toString(), pad);
    }

    public static String padRight(float val, int pad)
    {
        return padRight(Float.valueOf(val).toString(), pad);
    }

    public static String getTrans(int resid, Object... args)
    {
        return String.format(resources.getString(resid), args);
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
}
