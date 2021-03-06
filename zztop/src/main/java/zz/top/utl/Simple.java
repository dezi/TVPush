package zz.top.utl;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import android.content.ContentResolver;
import android.provider.Settings;
import android.os.Handler;
import android.os.Build;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
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

    @Nullable
    public static String hmacSha1UUID(String key, String data)
    {
        try
        {
            Key secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
            Mac instance = Mac.getInstance("HmacSHA1");
            instance.init(secretKeySpec);

            byte[] bytes = instance.doFinal(data.getBytes("UTF-8"));

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

            return Base64.encodeToString(buffer, 0 ,xfer, android.util.Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
