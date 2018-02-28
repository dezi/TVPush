package de.xavaro.android.p2pcamera;

import android.util.Log;

public class P2PReaderThreadAudio extends P2PReaderThread
{
    private static final String LOGTAG = P2PReaderThreadAudio.class.getSimpleName();

    public P2PReaderThreadAudio(P2PSession session, byte channel)
    {
        super(session, channel);
    }

    @Override
    public boolean handleData(byte[] data, int size)
    {
        P2PFrame head = P2PFrame.parse(data, session.isBigEndian);

        Log.d(LOGTAG, "handleData:"
                + " command=" + head.commandType
                + " seq=" + head.commandNumber
                + " exsize=" + head.exHeaderSize
                + " datasize=" + head.dataSize);

        return true;
    }
}
