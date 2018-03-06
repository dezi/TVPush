package zz.top.p2p.camera;

import android.util.Log;

import zz.top.p2p.api.P2PApiErrors;
import zz.top.p2p.api.P2PApiNative;

public abstract class P2PReaderThread extends Thread
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
            int headLen = P2PHeader.HEADER_SIZE;
            int frameLen = (channel == CHANNEL_COMMAND) ? P2PFrame.FRAME_SIZE : P2PAVFrame.FRAMEINFO_SIZE;

            byte[] headBuff = new byte[headLen + frameLen];
            int[] headSize = new int[]{headBuff.length};

            if (channel == CHANNEL_COMMAND)
            {
                Log.d(LOGTAG, "head: wait channel=" + channel + " size=" + headSize[0]);
            }

            int resHead = P2PApiNative.Read(session.session, channel, headBuff, headSize, -1);

            if ((resHead == P2PApiErrors.ERROR_P2P_SESSION_CLOSED_CALLED) || !session.isConnected)
            {
                Log.d(LOGTAG, "run: closed channel=" + this.channel);

                break;
            }

            if ((resHead != 0) || (headSize[0] != headBuff.length))
            {
                Log.d(LOGTAG, "run: read corrupt channel=" + this.channel + " resRead=" + resHead + " headSize=" + headSize[0]);

                session.isCorrupted = true;

                break;
            }

            P2PHeader header = P2PHeader.parse(headBuff, 0, session.isBigEndian);

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

            byte[] frameBuff = new byte[frameLen];
            System.arraycopy(headBuff, headLen, frameBuff, 0, frameLen);

            byte[] dataBuff = new byte[header.dataSize - frameLen];
            int[] dataSize = new int[]{dataBuff.length};

            if (channel == CHANNEL_COMMAND)
            {
                Log.d(LOGTAG, "data: wait channel=" + channel + " size=" + dataSize[0]);
            }

            int resData = P2PApiNative.Read(session.session, channel, dataBuff, dataSize, -1);

            if ((resData == P2PApiErrors.ERROR_P2P_SESSION_CLOSED_CALLED) || !session.isConnected)
            {
                Log.d(LOGTAG, "run: closed channel=" + this.channel);

                break;
            }

            if ((resData != 0) || (dataSize[0] != dataBuff.length))
            {
                Log.d(LOGTAG, "data: read corrupt channel=" + this.channel);

                session.isCorrupted = true;
                break;
            }

            if (channel == CHANNEL_COMMAND)
            {
                Log.d(LOGTAG, "data: read channel=" + channel + " res=" + resData + " size=" + dataSize[0]);
            }

            if (!handleData(frameBuff, frameBuff.length, dataBuff, dataBuff.length))
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

    public abstract boolean handleData(byte[] headBuff, int headSize, byte[] dataBuff, int dataSize);
}

