package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

public class AdbServicePull extends AdbService
{
    private static final String LOGTAG = AdbServicePull.class.getSimpleName();

    public AdbServicePull(Context context, String ipaddr, int ipport)
    {
        super(context, ipaddr, ipport);
    }

    protected boolean onStartService()
    {
        Log.d(LOGTAG, "onStartService: pull.");

        String remoteFile = onGetRemoteFileName();

        AdbStream stream = adb.openService("shell:cat < " + remoteFile);

        Log.d(LOGTAG, "onStartService: open service.");

        if (stream != null)
        {
            Log.d(LOGTAG, "onStartService: service opened.");

            onRemoteServiceOpen();

            while (!stream.isClosed())
            {
                byte[] data = stream.read();

                if (data != null) onRemoteDataReceived(data);
            }

            onRemoteServiceClose();

            Log.d(LOGTAG, "onStartService: service closed.");

            onServiceSuccess();

            return true;
        }
        else
        {
            onServiceFailed();

            return false;
        }
    };

    protected String onGetRemoteFileName()
    {
        Log.e(LOGTAG, "STUB!");

        return null;
    }

    protected void onRemoteServiceOpen()
    {
    }

    protected void onRemoteDataReceived(byte[] data)
    {
        Log.d(LOGTAG, "onRemoteDataReceived: size=" + data.length);
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
