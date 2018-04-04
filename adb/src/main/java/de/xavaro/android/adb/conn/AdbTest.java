package de.xavaro.android.adb.conn;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AdbTest
{
    private static final String LOGTAG = AdbTest.class.getSimpleName();

    public static void testCheck(final Context context, final String ipaddr, final int ipport)
    {
        AdbServiceCheck adbServiceCheck = new AdbServiceCheck(context, ipaddr, ipport);

        Log.d(LOGTAG, "onCreate: adbServiceCheck:" + adbServiceCheck.startSync());
    }

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

    public static void testPullPush(final Context context, final String ipaddr, final int ipport)
    {
        Thread test = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                AdbServicePull adbServicePull = new AdbServicePull(
                        context, ipaddr, ipport,
                        "/storage/E06D-EF93/sdb.xml");

                adbServicePull.setOutputStream(outputStream);
                boolean success = adbServicePull.startSync();

                Log.d(LOGTAG, "onCreate: AdbServicePull: startsync: success=" + success);

                if (success)
                {
                    Log.d(LOGTAG, "onCreate: AdbServicePull: startsync: size=" + adbServicePull.outputStream.size());

                    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

                    AdbServicePush adbServicePush = new AdbServicePush(
                            context, ipaddr, ipport,
                            "/storage/E06D-EF93/sdb.push.xml");

                    adbServicePush.setInputStream(inputStream);

                    success = adbServicePush.startSync();
                    Log.d(LOGTAG, "onCreate: AdbServicePush: startsync: success=" + success);
                }
            }
        });

        test.start();
    }
}
