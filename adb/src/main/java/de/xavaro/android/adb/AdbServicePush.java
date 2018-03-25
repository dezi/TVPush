package de.xavaro.android.adb;

import android.content.Context;
import android.util.Log;

public class AdbServicePush extends AdbService
{
    private static final String LOGTAG = AdbServicePush.class.getSimpleName();

    public AdbServicePush(Context context, String ipaddr, int ipport, String remoteFile)
    {
        super(context, ipaddr, ipport);

        this.remoteFile = remoteFile;
    }

    protected boolean onStartService()
    {
        Log.d(LOGTAG, "onStartService: push.");

        AdbStream stream = adb.openService("shell:cat > " + remoteFile);

        Log.d(LOGTAG, "onStartService: open service.");

        if (stream != null)
        {
            Log.d(LOGTAG, "onStartService: service opened.");

            success = true;

            onRemoteServiceOpen();

            int size = inputStream.available();
            int total = 0;

            while (!stream.isClosed())
            {
                int rest = inputStream.available();
                if (rest > maxdata) rest = maxdata;

                byte[] chunk = new byte[rest];

                try
                {
                    int xfer = inputStream.read(chunk);

                    Log.d(LOGTAG, "onStartService: push size=" + size + " rest=" + rest + " xfer=" + xfer);

                    stream.write(chunk);

                    total += xfer;

                    if (total == size) break;
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();

                    success = false;
                    break;
                }
            }

            onRemoteServiceClose();

            Log.d(LOGTAG, "onStartService: service closed.");

            return success;
        }

        return false;
    };
}
