package zz.top.utl;

public class Log
{
    public static boolean debug = false;

    public static String[] allow = new String[]
            {
                    "GLSRenderer",
                    "p2p*",
                    "gls*+"
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
        android.util.Log.e(logtag, message);
    }

    public static boolean checkLog(String[] checks, String logtag)
    {
        logtag = logtag.toLowerCase();

        for (String check : checks)
        {
            check = check.toLowerCase();

            if (check.endsWith("*") && logtag.startsWith(check.substring(0, check.length() - 2)))
            {
                return true;
            }

            if (check.equalsIgnoreCase(logtag))
            {
                return true;
            }
        }

        return false;
    }
}
