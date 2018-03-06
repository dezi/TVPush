package zz.top.p2p.camera;

import android.util.Log;

//import com.aac.utils.DecodeAAC;
import zz.top.dec.AACDecode;

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

        AACDecode.open();

        Log.d(LOGTAG, "onStart: done.");

        return true;
    }

    public boolean onStop()
    {
        AACDecode.close();

        decodeData = null;

        return true;
    }

    @Override
    public boolean handleData(byte[] headBuff, int headSize, byte[] dataBuff, int dataSize)
    {
        P2PAVFrame avFrame = new P2PAVFrame(headBuff, headSize, dataBuff, dataSize, session.isBigEndian);

        int nDecode = AACDecode.decode(avFrame.getFrameData(), avFrame.getFrameSize(), this.decodeData, this.decodeData.length);

        if ((avFrame.getFrameNo() % 30) == 0)
        {
            Log.d(LOGTAG, "handleData: " + avFrame.toFrameString() + " decoded=" + nDecode);
        }

        return true;
    }
}
