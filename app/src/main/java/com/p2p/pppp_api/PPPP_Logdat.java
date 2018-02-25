package com.p2p.pppp_api;

import android.util.Log;

public class PPPP_Logdat
{
    private static final String LOGTAG = PPPP_Logdat.class.getSimpleName();

    public static void logBytesShowHowtoCall()
    {
        logInt(1);
        logByte((byte) 1);
        logLong(1L);
        logString("*");
        logBoolean(true);

        logIntArray(new int[1]);
        logByteArray(new byte[1]);
        logByteArray(new byte[1], 1);
    }

    public static void logInt(int integer)
    {
        Log.d(LOGTAG, "logInt=" + Integer.toString(integer));
    }

    public static void logByte(byte byt)
    {
        Log.d(LOGTAG, "logByte=" + Byte.toString(byt));
    }

    public static void logLong(long longus)
    {
        Log.d(LOGTAG, "logLong=" + Long.toString(longus));
    }

    public static void logString(String str)
    {
        Log.d(LOGTAG, "logString=" + str);
    }

    public static void logBoolean(boolean bool)
    {
        Log.d(LOGTAG, "logBoolean=" + Boolean.toString(bool));
    }

    public static void logByteArray(Object bytes)
    {
        if (bytes == null)
        {
            Log.d(LOGTAG, "logByteArray=null");
        }
        else
        {
            if (bytes instanceof byte[])
            {
                Log.d(LOGTAG, "logByteArray=" + getHexBytesToString((byte[]) bytes, 0, ((byte[]) bytes).length));
            }
            else
            {
                Log.d(LOGTAG, "logByteArray=" + bytes.toString());
            }
        }
    }

    public static void logByteArray(Object bytes, int size)
    {
        if (bytes == null)
        {
            Log.d(LOGTAG, "logByteArray=null");
        }
        else
        {
            if (bytes instanceof byte[])
            {
                if (size > 32) size = 32;

                Log.d(LOGTAG, "logByteArray=" + getHexBytesToString((byte[]) bytes, 0, size));
            }
            else
            {
                Log.d(LOGTAG, "logByteArray=" + bytes.toString());
            }
        }
    }

    public static void logIntArray(Object integers)
    {
        if (integers == null)
        {
            Log.d(LOGTAG, "logIntArray=null");
        }
        else
        {
            if (integers instanceof int[])
            {
                Log.d(LOGTAG, "logIntArray=" + ((int[]) integers).length);

                for (int inx = 0; inx < ((int[]) integers).length; inx++)
                {
                    Log.d(LOGTAG, "logIntArray[" + inx + "]=" + ((int[]) integers)[inx]);
                }
            }
            else
            {
                Log.d(LOGTAG, "logIntArray=" + integers.toString());
            }
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
