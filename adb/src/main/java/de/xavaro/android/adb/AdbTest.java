package de.xavaro.android.adb;

import android.os.Environment;
import android.util.Log;

import java.io.File;

public class AdbTest
{
    private static final String LOGTAG = AdbConnection.class.getSimpleName();

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
                while (!stream.isClosed())
                {
                    try
                    {
                        byte[] data = stream.read();

                        if (data != null)
                        {
                            Log.d(LOGTAG, "main: " +  new String(data));
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                        return;
                    }
                }
            }
        }).start();

        try
        {
            stream.write("ls\n");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
