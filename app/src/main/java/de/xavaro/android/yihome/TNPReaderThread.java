package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;

public class TNPReaderThread extends Thread
{
    private static final String LOGTAG = TNPReaderThread.class.getSimpleName();

    private byte channel;
    private int sessionHandle;
    private boolean isByteOrderBig;

    public TNPReaderThread(int sessionHandle, int channel, boolean isByteOrderBig)
    {
        super();

        this.channel = (byte) channel;
        this.sessionHandle = sessionHandle;
        this.isByteOrderBig = isByteOrderBig;
    }

    @Override
    public void run()
    {
        while (true)
        {
            byte[] nBuffer = new byte[8];
            int[] nSize = new int[1];
            nSize[0] = 8;

            Log.d(LOGTAG, "head: wait channel=" + channel + " size=" + nSize[0]);
            int hRes = PPPP_APIs.PPPP_Read(sessionHandle, channel, nBuffer, nSize, -1);

            if ((hRes != 0) || (nSize[0] != 8))
            {
                Log.d(LOGTAG, "head: read corrupt...");
            }
            else
            {
                TNPHead header = TNPHead.parse(nBuffer, isByteOrderBig);

                Log.d(LOGTAG, "head: read"
                        + " channel=" + channel
                        + " ioType=" + header.ioType
                        + " version=" + header.version
                        + " datasize=" + header.dataSize);

                if ((header.dataSize < 0) || (header.dataSize > (1024 * 1024)))
                {
                    Log.d(LOGTAG, "head: size corrupt...");
                }
                else
                {
                    byte[] dBuffer =  new byte[ header.dataSize ];
                    int[] dSize = new int[1];
                    dSize[0] = header.dataSize;

                    Log.d(LOGTAG, "data: wait channel=" + channel + " size=" + dSize[0]);
                    int dRes = PPPP_APIs.PPPP_Read(sessionHandle, channel, dBuffer, dSize, -1);

                    if ((dRes != 0) || (dSize[0] != header.dataSize))
                    {
                        Log.d(LOGTAG, "head: read corrupt...");
                    }
                    else
                    {
                        Log.d(LOGTAG, "data: read channel=" + channel + " res=" + dRes + " size=" + dSize[0]);
                    }
                }
            }
        }
    }
}

