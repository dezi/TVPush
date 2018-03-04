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
                    Thread.sleep(10);
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
        try
        {
            if (avFrame.isIFrame() || haveIFrame)
            {
                haveIFrame = true;

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

                    haveIFrame = avFrame.isIFrame();
                }

                lastCodec = avFrame.getCodecId();
                lastWidth = avFrame.getVideoWidth();
                lastHeight = avFrame.getVideoHeight();

                if (haveIFrame)
                {
                    boolean ok;

                    synchronized (P2PLocks.decoderLock)
                    {
                        ok = decoder.decodeDecoder(avFrame.frmData, avFrame.getFrmSize(), (long) avFrame.getTimeStamp());
                    }

                    if (ok)
                    {
                        session.surface.setSourceDimensions(lastWidth, lastHeight);
                        session.surface.requestRender();

                        if (session.decodeFrames.size() > 2)
                        {
                            Log.d(LOGTAG, "handleData:"
                                    + " " + lastCodec
                                    + " " + lastWidth + "x" + lastHeight
                                    + " " + avFrame.getFrmNo()
                                    + " " + session.decodeFrames.size()
                            );
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            synchronized (P2PLocks.decoderLock)
            {
                session.surface.setSourceDecoder(null);

                decoder.releaseDecoder();
                decoder = null;
            }
        }

        return true;
    }
}
