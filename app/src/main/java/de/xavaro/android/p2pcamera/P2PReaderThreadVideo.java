package de.xavaro.android.p2pcamera;

import android.util.Log;

import de.xavaro.android.p2pcamera.p2pcommands.DeviceInfoData;
import de.xavaro.android.p2pcamera.p2pcommands.ResolutionData;

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
            P2PAVFrame.decryptIframe(aVFrame, session.targetPw + "0");
        }

        Log.d(LOGTAG, "handleData: " + aVFrame.toFrameString());

        return true;
    }
}
