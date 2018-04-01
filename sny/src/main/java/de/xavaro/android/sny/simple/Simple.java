package de.xavaro.android.sny.simple;

import android.support.annotation.Nullable;

import android.content.ContentResolver;
import android.content.res.Resources;
import android.content.Context;
import android.app.Application;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.provider.Settings;
import android.os.Handler;
import android.util.Base64;

import java.io.InputStream;

public class Simple
{
    private static Handler handler;
    private static Resources resources;
    private static WifiManager wifiManager;
    private static ContentResolver contentResolver;

    public static void initialize(Application app)
    {
        handler = new Handler();
        resources = app.getResources();
        wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
        contentResolver = app.getContentResolver();
    }

    public static Handler getHandler()
    {
        return handler;
    }

    @Nullable
    public static String getConnectedWifiName()
    {
        if (wifiManager == null) return null;

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

    public static String getDeviceUserName()
    {
        return Settings.Secure.getString(contentResolver, "bluetooth_name");
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

            return Base64.encodeToString(buffer, 0 ,xfer, android.util.Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
