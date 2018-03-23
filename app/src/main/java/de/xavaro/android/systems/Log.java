package de.xavaro.android.systems;

public class Log
{
    public static boolean debug = false;

    public static void d(String logtag, String message)
    {
        if (debug) android.util.Log.d(logtag, message);
    }

    public static void e(String logtag, String message)
    {
        android.util.Log.d(logtag, message);
    }
}
