package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class AdbServicePull extends AdbService
{
    private static final String LOGTAG = AdbServicePull.class.getSimpleName();

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

            while (!stream.isClosed())
            {
                byte[] data = stream.read();

                if (data != null)
                {
                    if (outputStream != null)
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
                    }
                    else
                    {
                        onRemoteDataReceived(data);
                    }
                }
            }

            onRemoteServiceClose();

            Log.d(LOGTAG, "onStartService: service closed.");

            return success;
        }

        return false;
    };

    protected void onRemoteDataReceived(byte[] data)
    {
        Log.d(LOGTAG, "onRemoteDataReceived: size=" + data.length);
    }
}
