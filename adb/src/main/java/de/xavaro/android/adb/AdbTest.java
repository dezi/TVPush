package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

public class AdbTest
{
    private static final String LOGTAG = AdbTest.class.getSimpleName();

    public static void testShell(final Context context, final String ipaddr, final int ipport)
    {
        Thread test = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(LOGTAG, "testShell: ip=" + ipaddr + " port=" + ipport);

                AdbConn adb = new AdbConn(context, ipaddr, ipport);

                Log.d(LOGTAG, "testShell: connect.");

                if (adb.connect())
                {
                    Log.d(LOGTAG, "testShell: connected.");

                    AdbStream stream = adb.openService("shell:ls -al /storage/E06D-EF93");

                    Log.d(LOGTAG, "testShell: open service.");

                    if (stream != null)
                    {
                        Log.d(LOGTAG, "testShell: service opened.");

                        while (!stream.isClosed())
                        {
                            byte[] data = stream.read();

                            Log.d(LOGTAG, "testShell: read=\n"
                                    + ((data == null) ? null : new String(data)));
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
