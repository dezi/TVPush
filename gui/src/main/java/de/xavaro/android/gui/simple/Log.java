package de.xavaro.android.gui.simple;

public class Log
{
    public static boolean debug = false;

    public static String[] allow = new String[]
            {
                    "gui*"
            };

    public static String[] deny = new String[]
            {
                    "GUISpeechListener"
            };

    public static void d(String logtag, String message)
    {
        if (debug || (checkLog(allow, logtag) && ! checkLog(deny, logtag)))
        {
            android.util.Log.d(logtag, message);
        }
    }

    public static void e(String logtag, String message)
    {
        android.util.Log.d(logtag, message);
    }

    private static boolean checkLog(String[] allows, String logtag)
    {
        logtag = logtag.toLowerCase();

        for (String allow : allows)
        {
            if (allow.endsWith("*") && logtag.startsWith(allow.substring(0, allow.length() -2 )))
            {
                return true;
            }
        }

        return false;
    }
}
