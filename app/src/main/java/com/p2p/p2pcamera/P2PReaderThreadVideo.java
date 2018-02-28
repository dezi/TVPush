package com.p2p.p2pcamera;

import android.util.Log;

public class P2PReaderThreadVideo extends P2PReaderThread
{
    private static final String LOGTAG = P2PReaderThreadVideo.class.getSimpleName();

    public P2PReaderThreadVideo(P2PSession session, byte channel)
    {
        super(session, channel);
    }

    @Override
    public boolean handleData(byte[] data, int size)
    {
        P2PAVFrame aVFrame = new P2PAVFrame(data, size, session.isBigEndian);

        if (session.isEncrypted && aVFrame.isIFrame())
        {
            session.p2pAVFrameDecrypt.decryptIframe(aVFrame);

            Log.d(LOGTAG, "handleData: " + aVFrame.getCodecName() + " " + aVFrame.toFrameString());
        }

        return true;
    }
}
