package de.xavaro.android.awx.simple;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
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
    private static WifiManager wifiManager;

    public static void initialize(Application app)
    {
        resources = app.getResources();
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

    public static boolean isUIThread()
    {
        return (Looper.getMainLooper().getThread() == Thread.currentThread());
    }

    public static void runBackground(Runnable runnable)
    {
        if (isUIThread())
        {
            new Thread(runnable).start();
        }
        else
        {
            runnable.run();
        }
    }
}
