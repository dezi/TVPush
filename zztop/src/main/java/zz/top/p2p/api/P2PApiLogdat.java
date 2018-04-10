package zz.top.p2p.api;

import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({"WeakerAccess", "unused"})
public class P2PApiLogdat
{
    private static final String LOGTAG = P2PApiLogdat.class.getSimpleName();

    public static void logShowHowtoCall()
    {
        logInt(1);
        logByte((byte) 1);
        logLong(1L);
        logString("test");
        logBoolean(true);

        logObject(new Object());
        logIntArray(new int[1]);
        logByteArray(new byte[1]);
        logByteArray(new byte[1], 1);

        logMapStringString(new HashMap<String, String>());
    }

    public static void logInt(int integer)
    {
        Log.e(LOGTAG, "logInt=" + Integer.toString(integer));
    }

    public static void logByte(byte byt)
    {
        Log.e(LOGTAG, "logByte=" + Byte.toString(byt));
    }

    public static void logLong(long longus)
    {
        Log.e(LOGTAG, "logLong=" + Long.toString(longus));
    }

    public static void logObject(Object str)
    {
        Log.e(LOGTAG, "logObject=" + str);
    }

    public static void logString(String str)
    {
        Log.e(LOGTAG, "logString=" + str);
    }

    public static void logBoolean(boolean bool)
    {
        Log.e(LOGTAG, "logBoolean=" + Boolean.toString(bool));
    }

    public static void logByteArrayString(Object bytes)
    {
        if (bytes == null)
        {
            Log.e(LOGTAG, "logByteArrayString=null");
        }
        else
        {
            if (bytes instanceof byte[])
            {
                Log.e(LOGTAG, "logByteArrayString=" + new String((byte[]) bytes));
            }
            else
            {
                Log.e(LOGTAG, "logByteArrayString=" + bytes.toString());
            }
        }
    }

    public static void logByteArray(Object bytes)
    {
        if (bytes == null)
        {
            Log.e(LOGTAG, "logByteArray=null");
        }
        else
        {
            if (bytes instanceof byte[])
            {
                Log.e(LOGTAG, "logByteArray=" + getHexBytesToString((byte[]) bytes, 0, ((byte[]) bytes).length));
            }
            else
            {
                Log.e(LOGTAG, "logByteArray=" + bytes.toString());
            }
        }
    }

    public static void logByteArray(Object bytes, int size)
    {
        if (bytes == null)
        {
            Log.e(LOGTAG, "logByteArray=null");
        }
        else
        {
            if (bytes instanceof byte[])
            {
                if (size > 32) size = 32;

                Log.e(LOGTAG, "logByteArray=" + getHexBytesToString((byte[]) bytes, 0, size));
            }
            else
            {
                Log.e(LOGTAG, "logByteArray=" + bytes.toString());
            }
        }
    }

    public static void logIntArray(Object integers)
    {
        if (integers == null)
        {
            Log.e(LOGTAG, "logIntArray=null");
        }
        else
        {
            if (integers instanceof int[])
            {
                Log.e(LOGTAG, "logIntArray=" + ((int[]) integers).length);

                for (int inx = 0; inx < ((int[]) integers).length; inx++)
                {
                    Log.e(LOGTAG, "logIntArray[" + inx + "]=" + ((int[]) integers)[inx]);
                }
            }
            else
            {
                Log.e(LOGTAG, "logIntArray=" + integers.toString());
            }
        }
    }

    public static void logMapStringString(Map<String, String> map)
    {
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            Log.e(LOGTAG, "logMapStringString: key=" + entry.getKey() + " val=" + entry.getValue());
        }
    }

    public static String getHexBytesToString(byte[] bytes, int offset, int length)
    {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[ length << 1 ];

        for (int inx = offset; inx < (length + offset); inx++)
        {
            //noinspection PointlessArithmeticExpression
            hexChars[ ((inx - offset) << 1) + 0 ] = hexArray[ (bytes[ inx ] >> 4) & 0x0f ];
            //noinspection PointlessBitwiseExpression
            hexChars[ ((inx - offset) << 1) + 1 ] = hexArray[ (bytes[ inx ] >> 0) & 0x0f ];
        }

        return String.valueOf(hexChars);
    }
}
