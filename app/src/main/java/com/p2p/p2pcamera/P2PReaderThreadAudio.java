package com.p2p.p2pcamera;

import android.util.Log;

import com.aac.utils.DecodeAAC;

public class P2PReaderThreadAudio extends P2PReaderThread
{
    private static final String LOGTAG = P2PReaderThreadAudio.class.getSimpleName();

    private byte[] decodeData;

    public P2PReaderThreadAudio(P2PSession session, byte channel)
    {
        super(session, channel);
    }

    public boolean onStart()
    {
        decodeData = new byte[ 10 * 1024 ];

        DecodeAAC.nOpen();

        Log.d(LOGTAG, "onStart: done.");

        return true;
    }

    public boolean onStop()
    {
        DecodeAAC.nClose();

        decodeData = null;

        return true;
    }

    @Override
    public boolean handleData(byte[] data, int size)
    {
        P2PAVFrame avFrame = new P2PAVFrame(data, size, session.isBigEndian);

        int nDecode = DecodeAAC.nDecode(avFrame.frmData, avFrame.getFrmSize(), this.decodeData, this.decodeData.length);

        if ((avFrame.getFrmNo() % 30) == 0)
        {
            Log.d(LOGTAG, "handleData: " + avFrame.toFrameString() + " decoded=" + nDecode);
        }

        return true;
    }
}
