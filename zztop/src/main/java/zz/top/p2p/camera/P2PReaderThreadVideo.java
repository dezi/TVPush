package zz.top.p2p.camera;

public class P2PReaderThreadVideo extends P2PReaderThread
{
    private static final String LOGTAG = P2PReaderThreadVideo.class.getSimpleName();

    public P2PReaderThreadVideo(P2PSession session, byte channel)
    {
        super(session, channel);

        this.setPriority(MAX_PRIORITY);
    }

    private int frameNoOverflows;
    private int lastFrameNo;

    @Override
    public boolean handleData(byte[] headBuff, int headSize, byte[] dataBuff, int dataSize)
    {
        P2PAVFrame avFrame = new P2PAVFrame(headBuff, headSize, dataBuff, dataSize, session.isBigEndian);

        //
        // Fuck dat idiot making frame count a short and relying on
        // the frame count to order I-Frames correctly.
        //
        // R.I.P idiot. You saved two bytes.
        //

        if (avFrame.getFrameNo() < lastFrameNo) frameNoOverflows++;
        lastFrameNo = avFrame.getFrameNo();
        avFrame.addFrameNoOverflow(frameNoOverflows);

        if (session.isEncrypted && avFrame.isIFrame())
        {
            session.p2pAVFrameDecrypt.decryptIframe(avFrame);
        }

        //Log.d(LOGTAG, "handleData: channel=" + channel + " " + avFrame.toFrameString());

        session.renderFrame(avFrame);

        return true;
    }
}
