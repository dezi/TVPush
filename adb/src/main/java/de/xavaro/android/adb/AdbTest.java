package de.xavaro.android.adb;

import android.util.Log;

public class AdbTest
{
    private static final String LOGTAG = AdbTest.class.getSimpleName();

    public static void testShell()
    {
        Log.d(LOGTAG, "testShell: open.");

        AdbConn adb = new AdbConn("192.168.0.11", 5555);

        Log.d(LOGTAG, "testShell: connect.");

        if (adb.connect())
        {
            Log.d(LOGTAG, "testShell: connected.");

            AdbStream stream = adb.openService("shell:ls -al /storage");

            Log.d(LOGTAG, "testShell: open service.");

            if (stream != null)
            {
                Log.d(LOGTAG, "testShell: service opened.");

                while (!stream.isClosed())
                {
                    byte[] data = stream.read();

                    Log.d(LOGTAG, "testShell: read=" + ((data == null) ? null : new String(data)));
                }

                Log.d(LOGTAG, "testShell: service closed.");
            }

            adb.close();

            Log.d(LOGTAG, "testShell: connection closed.");
        }
        else
        {
            Log.d(LOGTAG, "testShell: connection failed.");
        }
   }
}
