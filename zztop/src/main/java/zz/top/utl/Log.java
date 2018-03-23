package zz.top.utl;

public class Log
{
    public static boolean debug = false;

    public static String[] allow = new String[]
            {
                    "p2p*"
            };

    public static void d(String logtag, String message)
    {
        if (debug || checkLog(allow, logtag))
        {
            android.util.Log.d(logtag, message);
        }
    }

    public static void e(String logtag, String message)
    {
        android.util.Log.d(logtag, message);
    }


    public static boolean checkLog(String[] allows, String logtag)
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
