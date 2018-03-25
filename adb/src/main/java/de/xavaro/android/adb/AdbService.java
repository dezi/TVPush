package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public abstract class AdbService
{
    private static final String LOGTAG = AdbService.class.getSimpleName();

    protected Context context;
    protected String ipaddr;
    protected int ipport;
    protected int maxdata;

    protected Thread thread;
    protected AdbConn adb;

    public boolean success;
    public String remoteFile;

    public ByteArrayInputStream inputStream;
    public ByteArrayOutputStream outputStream;

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

    public void setOutputStream(ByteArrayOutputStream outputStream)
    {
        this.outputStream = outputStream;
    }

    public void setInputStream(ByteArrayInputStream inputStream)
    {
        this.inputStream = inputStream;
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

                maxdata = adb.getMaxData();

                success = onStartService();

                if (success)
                {
                    onServiceSuccess();
                }
                else
                {
                    onServiceFailed();
                }

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

    protected abstract boolean onStartService();

    protected void onConnectFailed()
    {
    }

    protected void onRemoteServiceOpen()
    {
    }

    protected void onRemoteDataReceived(byte[] data)
    {
    }

    protected void onRemoteServiceClose()
    {

    }
    protected void onServiceSuccess()
    {
    }

    protected void onServiceFailed()
    {
    }
}
