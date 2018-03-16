package zz.top.utl;

import android.content.ContentResolver;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class Simple
{
    private static Handler handler;
    private static WifiManager wifiManager;
    private static ContentResolver contentResolver;

    public static void initialize(Application app)
    {
        handler = new Handler();

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
}
