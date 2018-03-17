package de.xavaro.android.iot.proxim;

import android.util.Log;

import de.xavaro.android.iot.base.IOT;

public class IOTProximScanner extends Thread
{
    private static final String LOGTAG = IOTProximScanner.class.getName();

    public static void startService()
    {
        if (IOT.instance == null) return;

        if (IOT.instance.proximScanner == null)
        {
            IOT.instance.proximScanner = new IOTProximScanner();
            IOT.instance.proximScanner.start();
        }
    }

    @Override
    public void run()
    {
        Log.d(LOGTAG, "run: start.");

        while ((IOT.instance != null) && (IOT.instance.proximScanner == this))
        {

        }

        Log.d(LOGTAG, "run: done.");
    }
}
