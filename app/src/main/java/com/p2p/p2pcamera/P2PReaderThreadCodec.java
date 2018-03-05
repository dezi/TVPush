package com.p2p.p2pcamera;

import android.util.Log;

import zz.top.dec.VIDDecode;

public class P2PReaderThreadCodec extends Thread
{
    private static final String LOGTAG = P2PReaderThreadCodec.class.getSimpleName();

    static final int MAX_FRAMEBUF = 4000000;

    private P2PSession session;
    private P2PVideoGLDecoder decoder;
    private boolean haveIFrame;

    private int lastCodec;
    private int lastWidth;
    private int lastHeight;

    private int modcount;
    private int lastframes;
    private long lasttimems;

    public P2PReaderThreadCodec(P2PSession session)
    {
        super();

        this.session = session;
    }

    @Override
    public void run()
    {
        Log.d(LOGTAG, "run: start.");

        onStart();

        while (session.isConnected)
        {
            P2PAVFrame avFrame = null;

            synchronized (session.decodeFrames)
            {
                if (session.decodeFrames.size() > 0)
                {
                    avFrame = session.decodeFrames.remove(0);
                }
            }

            if (avFrame != null)
            {
                handleData(avFrame);
            }
            else
            {
                try
                {
                    Thread.sleep(3);
                }
                catch (Exception ignore)
                {
                }
            }
        }

        onStop();

        Log.d(LOGTAG, "run: stop.");
    }

    public boolean onStart()
    {
        lastCodec = 0;
        lastWidth = 0;
        lastHeight = 0;

        Log.d(LOGTAG, "onStart: done.");

        return true;
    }

    public boolean onStop()
    {
        synchronized (P2PLocks.decoderLock)
        {
            session.surface.setSourceDecoder(null);

            decoder.releaseDecoder();
            decoder = null;
        }

        Log.d(LOGTAG, "onStop: done.");

        return true;
    }

    public boolean handleData(P2PAVFrame avFrame)
    {
        if (lasttimems == 0)
        {
            lastframes = 0;
            lasttimems = System.currentTimeMillis();
        }
        else
        {
            long diffmillis = System.currentTimeMillis() - lasttimems;

            if (diffmillis >= 1000)
            {
                Log.d(LOGTAG, "handleData: fps=" + lastframes);

                lastframes = 0;
                lasttimems = System.currentTimeMillis();
            }
        }

        lastframes++;

        if ((decoder == null)
                || (lastCodec != avFrame.getCodecId())
                || (lastWidth != avFrame.getVideoWidth())
                || (lastHeight != avFrame.getVideoHeight()))
        {
            synchronized (P2PLocks.decoderLock)
            {
                if (decoder != null)
                {
                    Log.d(LOGTAG, "handleData: releaseDecoder codec=" + lastCodec);

                    session.surface.setSourceDecoder(null);

                    decoder.releaseDecoder();
                    decoder = null;
                }

                decoder = new VIDDecode(avFrame.getCodecId());

                session.surface.setSourceDecoder(decoder);
            }

            Log.d(LOGTAG, "handleData: createDecoder codec=" + avFrame.getCodecId());
        }

        lastCodec = avFrame.getCodecId();
        lastWidth = avFrame.getVideoWidth();
        lastHeight = avFrame.getVideoHeight();

        boolean ok;

        ok = decoder.decodeDecoder(avFrame.frmData, avFrame.getFrmSize(), 0);

        if (ok)
        {
            session.surface.setSourceDimensions(lastWidth, lastHeight);
            session.surface.requestRender();
        }

        if (((modcount++ % 30) == 0) || !ok)
        {
            Log.d(LOGTAG, "handleData:"
                    + " " + ok
                    + " " + lastCodec
                    + " " + lastWidth + "x" + lastHeight
                    + " " + avFrame.getFrmNo()
                    + " " + avFrame.getFrmSize()
                    + " " + session.decodeFrames.size()
            );
        }

        return true;
    }
}
