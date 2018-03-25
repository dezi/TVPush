package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

public class AdbTest
{
    private static final String LOGTAG = AdbTest.class.getSimpleName();

    public static void testShell(final Context context)
    {
        Thread test = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(LOGTAG, "testShell: open.");

                AdbConn adb = new AdbConn(context, "192.168.0.11", 5555);

                Log.d(LOGTAG, "testShell: connect.");

                if (adb.connect())
                {
                    Log.d(LOGTAG, "testShell: connected.");

                    AdbStream stream = adb.openService("shell:cat < /storage/E06D-EF93/yyy.txt");

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
                    Log.e(LOGTAG, "testShell: connection failed.");
                }
            }
        });

        test.start();
    }
}
