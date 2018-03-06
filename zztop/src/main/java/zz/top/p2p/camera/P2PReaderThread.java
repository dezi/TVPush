package zz.top.p2p.camera;

import android.util.Log;

import zz.top.p2p.api.P2PApiErrors;
import zz.top.p2p.api.P2PApiNative;

public class P2PReaderThread extends Thread
{
    public static final String LOGTAG = P2PReaderThread.class.getSimpleName();

    public final static byte CHANNEL_COMMAND = 0;

    public P2PSession session;
    public byte channel;

    public P2PReaderThread(P2PSession session, byte channel)
    {
        super();

        this.session = session;
        this.channel = channel;

        setPriority(MAX_PRIORITY);
    }

    @Override
    public void run()
    {
        Log.d(LOGTAG, "run: start channel=" + this.channel);

        onStart();

        while (session.isConnected)
        {
            
            byte[] nBuffer = new byte[P2PHeader.HEADER_SIZE];
            int[] nSize = new int[]{nBuffer.length};

            if (channel == CHANNEL_COMMAND)
            {
                Log.d(LOGTAG, "head: wait channel=" + channel + " size=" + nSize[0]);
            }

            int resHead = P2PApiNative.Read(session.session, channel, nBuffer, nSize, -1);

            if ((resHead == P2PApiErrors.ERROR_P2P_SESSION_CLOSED_CALLED) || !session.isConnected)
            {
                Log.d(LOGTAG, "run: closed channel=" + this.channel);

                break;
            }

            if ((resHead != 0) || (nSize[0] != nBuffer.length))
            {
                Log.d(LOGTAG, "run: read corrupt channel=" + this.channel + " resRead=" + resHead + " nSize=" + nSize[0]);

                session.isCorrupted = true;

                break;
            }

            P2PHeader header = P2PHeader.parse(nBuffer, 0, session.isBigEndian);

            if (channel == CHANNEL_COMMAND)
            {
                Log.d(LOGTAG, "head: read"
                        + " channel=" + channel
                        + " ioType=" + header.ioType
                        + " version=" + header.version
                        + " datasize=" + header.dataSize);
            }

            if ((header.dataSize < 0) || (header.dataSize > (2 * 1024 * 1024)))
            {
                Log.d(LOGTAG, "head: size corrupt...");
                session.isCorrupted = true;

                break;
            }

            byte[] dBuffer = new byte[header.dataSize];
            int[] dSize = new int[]{dBuffer.length};

            if (channel == CHANNEL_COMMAND)
            {
                Log.d(LOGTAG, "data: wait channel=" + channel + " size=" + dSize[0]);
            }

            int resData = P2PApiNative.Read(session.session, channel, dBuffer, dSize, -1);

            if ((resData == P2PApiErrors.ERROR_P2P_SESSION_CLOSED_CALLED) || !session.isConnected)
            {
                Log.d(LOGTAG, "run: closed channel=" + this.channel);

                break;
            }

            if ((resData != 0) || (dSize[0] != dBuffer.length))
            {
                Log.d(LOGTAG, "data: read corrupt channel=" + this.channel);

                session.isCorrupted = true;
                break;
            }

            if (channel == CHANNEL_COMMAND)
            {
                Log.d(LOGTAG, "data: read channel=" + channel + " res=" + resData + " size=" + dSize[0]);
            }

            if (!handleData(dBuffer, dSize[0]))
            {
                session.isCorrupted = true;
                break;
            }
        }

        if (session.isCorrupted)
        {
            session.forceDisconnect();
        }

        onStop();

        Log.d(LOGTAG, "run: stop channel=" + this.channel);
    }

    public boolean onStart()
    {
        return true;
    }

    public boolean onStop()
    {
        return true;
    }

    public boolean handleData(byte[] data, int size)
    {
        return true;
    }
}

