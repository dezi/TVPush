package de.xavaro.android.adb.conn;

import android.content.Context;
import android.util.Log;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class AdbTest
{
    private static final String LOGTAG = AdbTest.class.getSimpleName();

    public static boolean getADBConfigured(String ipaddr, int ipport)
    {
        try
        {
            Log.d(LOGTAG, "getADBConfigured: ipaddr=" + ipaddr + " ipport=" + ipport);

            Socket socket = new Socket(ipaddr, ipport);
            socket.setSoTimeout(3000);

            Log.d(LOGTAG, "getADBConfigured: open socket done.");

            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = socket.getOutputStream();

            Log.d(LOGTAG, "getADBConfigured: get streams done.");

            inputStream.close();
            outputStream.close();

            socket.close();

            Log.d(LOGTAG, "getADBConfigured: is configured.");

            return true;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, "getADBConfigured: not configured.");
        }

        return false;
    }

    public static boolean getADBAuthorized(Context context, String ipaddr, int ipport)
    {
        AdbServiceCheck adbServiceCheck = new AdbServiceCheck(context, ipaddr, ipport);

        boolean authorized = adbServiceCheck.startSync();

        Log.d(LOGTAG, "getADBAuthorized: authorized=" + authorized);

        return authorized;
    }
}
