package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_APIs;

public class P2PReaderThread extends Thread
{
    private static final String LOGTAG = P2PReaderThread.class.getSimpleName();

    public final static int CHANNEL_COMMAND = 0;

    protected byte channel;
    protected int sessionHandle;
    protected boolean isByteOrderBig;

    public P2PReaderThread(int sessionHandle, int channel, boolean isByteOrderBig)
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

                break;
            }
            else
            {
                P2PHeader header = P2PHeader.parse(nBuffer, isByteOrderBig);

                Log.d(LOGTAG, "head: read"
                        + " channel=" + channel
                        + " ioType=" + header.ioType
                        + " version=" + header.version
                        + " datasize=" + header.dataSize);

                if ((header.dataSize < 0) || (header.dataSize > (1024 * 1024)))
                {
                    Log.d(LOGTAG, "head: size corrupt...");

                    break;
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
                        Log.d(LOGTAG, "data: read corrupt...");

                        break;
                    }
                    else
                    {
                        Log.d(LOGTAG, "data: read channel=" + channel + " res=" + dRes + " size=" + dSize[0]);

                        handleData(dBuffer, dSize[0]);
                    }
                }
            }
        }
    }

    public void handleData(byte[] data, int size)
    {
    }
}

