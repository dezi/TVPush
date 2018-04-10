package de.xavaro.android.edx.simple;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Base64;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.Key;
import java.util.Map;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Simple
{
    private static Resources resources;
    private static SharedPreferences prefs;
    private static WifiManager wifiManager;

    public static void initialize(Application app)
    {
        resources = app.getResources();
        prefs = PreferenceManager.getDefaultSharedPreferences(app);
        wifiManager = (WifiManager) app.getSystemService(Context.WIFI_SERVICE);
    }

    @Nullable
    public static String hmacSha1UUID(String key, String data)
    {
        try
        {
            Key secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA1");
            Mac instance = Mac.getInstance("HmacSHA1");
            instance.init(secretKeySpec);

            byte[] bytes = instance.doFinal(data.getBytes());

            ByteBuffer bb = ByteBuffer.wrap(bytes,0 , 16);

            long high = bb.getLong();
            long low = bb.getLong();

            UUID uuid = new UUID(high, low);

            return uuid.toString();
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    public static SharedPreferences getPrefs()
    {
        return prefs;
    }

    @Nullable
    public static String getConnectedWifiName()
    {
        if (wifiManager == null) return null;

        String wifi = wifiManager.getConnectionInfo().getSSID();
        return wifi.replace("\"", "");
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

            return Base64.encodeToString(buffer, 0 ,xfer, Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
