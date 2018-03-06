package zz.top.p2p.camera;

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
        P2PAVFrame avFrame = new P2PAVFrame(data, size, session.isBigEndian);

        if (session.isEncrypted && avFrame.isIFrame())
        {
            session.p2pAVFrameDecrypt.decryptIframe(avFrame);
        }

        //Log.d(LOGTAG, "handleData: channel=" + channel + " " + avFrame.toFrameString());

        session.renderFrame(avFrame);

        return true;
    }
}
