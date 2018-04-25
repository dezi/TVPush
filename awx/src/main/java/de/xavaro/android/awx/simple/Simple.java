package de.xavaro.android.awx.simple;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
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
    private static Handler handler;
    private static Resources resources;
    private static BluetoothManager bluetoothManager;
    private static BluetoothAdapter bluetoothAdapter;

    public static void initialize(Application app)
    {
        handler = new Handler();
        resources = app.getResources();

        bluetoothManager = (BluetoothManager) app.getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager != null)
        {
            bluetoothAdapter = bluetoothManager.getAdapter();
        }
    }

    public static Handler getHandler()
    {
        return handler;
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

    public static String getBytesToHexString(byte[] buffer)
    {
        StringBuilder hex = new StringBuilder();

        for (int inx = 0; inx < buffer.length; inx++)
        {
            String ccc = Integer.toHexString(buffer[inx] & 0xff);
            hex.append((ccc.length() < 2) ? "0" + ccc : ccc);
        }

        return hex.toString();
    }

    public static int colorRGB(int hue, int saturation, int brightness)
    {
        float[] hsv = new float[3];

        hsv[ 0 ] = hue;
        hsv[ 1 ] = saturation / 100f;
        hsv[ 2 ] = brightness / 100f;

        return Color.HSVToColor(hsv);
    }
}
