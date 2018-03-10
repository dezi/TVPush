package zz.top.utl;

import android.app.Application;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

public class Simple
{
    private static WifiManager wifiManager;

    public static void initialize(Application context)
    {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    @Nullable
    public static String getConnectedWifiName()
    {
        if (wifiManager == null) return null;

        String wifi = wifiManager.getConnectionInfo().getSSID();
        return wifi.replace("\"", "");
    }
}
