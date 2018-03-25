package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

public abstract class AdbService
{
    private static final String LOGTAG = AdbService.class.getSimpleName();

    protected Context context;
    protected String ipaddr;
    protected int ipport;

    protected Thread thread;
    protected AdbConn adb;

    protected boolean success;

    public AdbService(final Context context, final String ipaddr, final int ipport)
    {
        this.context = context;
        this.ipaddr = ipaddr;
        this.ipport = ipport;
    }

    public void start()
    {
        thread = new Thread(runner);
        thread.start();
    }

    public boolean startSync()
    {
        start();

        try
        {
            thread.join();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return success;
    }

    private Runnable runner = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "run: ip=" + ipaddr + " port=" + ipport);

            adb = new AdbConn(context, ipaddr, ipport);

            Log.d(LOGTAG, "run: connect.");

            if (adb.connect())
            {
                Log.d(LOGTAG, "run: connected.");

                success = onStartService();

                adb.close();

                Log.d(LOGTAG, "run: connection closed.");
            }
            else
            {
                Log.e(LOGTAG, "run: connection failed.");

                onConnectFailed();
            }
        }
    };

    protected void onConnectFailed()
    {
    }

    protected abstract boolean onStartService();
}
