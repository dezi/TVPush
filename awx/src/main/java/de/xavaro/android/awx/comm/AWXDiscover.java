package de.xavaro.android.awx.comm;

import android.util.Log;

import java.net.DatagramSocket;

import de.xavaro.android.awx.base.AWX;

public class AWXDiscover
{
    private static final String LOGTAG = AWXDiscover.class.getSimpleName();

    public static void startService()
    {
        if ((AWX.instance != null) && (AWX.instance.discover == null))
        {
            AWX.instance.discover = new AWXDiscover();
            AWX.instance.discover.startThread();
        }
    }

    public static void stopService()
    {
        if ((AWX.instance != null) && (AWX.instance.discover != null))
        {
            AWX.instance.discover.stopThread();
            AWX.instance.discover = null;
        }
    }

    private Thread discoverThread;
    private DatagramSocket socket;
    private final Object mutex = new Object();

    private void startThread()
    {
        synchronized (mutex)
        {
            if (discoverThread == null)
            {
                discoverThread = new Thread(discoverRunnable);
                discoverThread.start();
            }
        }
    }

    private void stopThread()
    {
        synchronized (mutex)
        {
            if (discoverThread != null)
            {
                discoverThread.interrupt();
                discoverThread = null;
            }
        }
    }

    private final Runnable discoverRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "discoverRunnable: start.");


            Log.d(LOGTAG, "discoverRunnable: done.");
        }
    };
}
