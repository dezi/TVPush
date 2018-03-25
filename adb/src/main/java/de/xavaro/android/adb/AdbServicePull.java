package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class AdbServicePull extends AdbService
{
    private static final String LOGTAG = AdbServicePull.class.getSimpleName();

    public String remoteFile;
    public ByteArrayOutputStream outputStream;

    public AdbServicePull(Context context, String ipaddr, int ipport, String remoteFile)
    {
        super(context, ipaddr, ipport);

        this.remoteFile = remoteFile;
    }

    protected boolean onStartService()
    {
        Log.d(LOGTAG, "onStartService: pull.");

        AdbStream stream = adb.openService("shell:cat < " + remoteFile);

        Log.d(LOGTAG, "onStartService: open service.");

        if (stream != null)
        {
            Log.d(LOGTAG, "onStartService: service opened.");

            success = true;

            onRemoteServiceOpen();

            outputStream = new ByteArrayOutputStream();

            while (!stream.isClosed())
            {
                byte[] data = stream.read();

                if (data != null)
                {
                    try
                    {
                        outputStream.write(data);
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();

                        success = false;
                        break;
                    }

                    onRemoteDataReceived(data);
                }
            }

            onRemoteServiceClose();

            Log.d(LOGTAG, "onStartService: service closed.");

            onServiceSuccess();

            return success;
        }
        else
        {
            onServiceFailed();

            return false;
        }
    };

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
