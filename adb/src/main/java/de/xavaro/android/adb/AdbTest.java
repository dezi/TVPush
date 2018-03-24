package de.xavaro.android.adb;

import android.util.Log;

public class AdbTest
{
    private static final String LOGTAG = AdbTest.class.getSimpleName();

    public static AdbStream stream;

    public static void main()
    {
        AdbConnection adb;

        try
        {
            adb = new AdbConnection("192.168.0.11", 5555);
            adb.connect();

            stream = adb.openService("shell:");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            return;
        }

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                Log.d(LOGTAG, "main: start reading.");

                while (!stream.isClosed())
                {
                    try
                    {
                        byte[] data = stream.read();

                        if (data != null)
                        {
                            Log.d(LOGTAG, "main: read=" +  new String(data));
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        return;
                    }
                }

                Log.d(LOGTAG, "main: done reading.");
            }
        }).start();

        try
        {
            stream.write("ls -al /storage\n");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
