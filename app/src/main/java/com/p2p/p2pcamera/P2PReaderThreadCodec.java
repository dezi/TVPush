package com.p2p.p2pcamera;

import android.opengl.GLES20;
import android.util.Log;

import com.decoder.xiaomi.AntsDecoder;
import com.decoder.xiaomi.DecoderBase;

public class P2PReaderThreadCodec extends Thread
{
    private static final String LOGTAG = P2PReaderThreadCodec.class.getSimpleName();

    static final int MAX_FRAMEBUF = 4000000;

    private P2PSession session;
    private DecoderBase decoder;
    private boolean haveIFrame;

    private int lastCodec;
    private int lastWidth;
    private int lastHeight;

    private int[] mYUVTextures;
    private P2PVideoStillImage mYUVImage;

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

        mYUVTextures = new int[3];
        GLES20.glGenTextures(mYUVTextures.length, mYUVTextures, 0);

        session.surface.setYUVTextureIds(mYUVTextures);

        Log.d(LOGTAG, "onStart: done.");

        return true;
    }

    public boolean onStop()
    {
        decoder.releaseDecoder();
        decoder = null;

        if (mYUVTextures != null)
        {
            GLES20.glDeleteTextures(mYUVTextures.length, mYUVTextures, 0);
            mYUVTextures = null;
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
                    if (decoder != null)
                    {
                        Log.d(LOGTAG, "handleData: releaseDecoder codec=" + lastCodec);

                        decoder.releaseDecoder();
                    }

                    decoder = new AntsDecoder(avFrame.getCodecId());

                    Log.d(LOGTAG, "handleData: createDecoder codec=" + avFrame.getCodecId());

                    haveIFrame = avFrame.isIFrame();
                }

                lastCodec = avFrame.getCodecId();
                lastWidth = avFrame.getVideoWidth();
                lastHeight = avFrame.getVideoHeight();

                if (haveIFrame)
                {
                    if (decoder.decodeDecoder(avFrame.frmData, avFrame.getFrmSize(), (long) avFrame.getTimeStamp()))
                    {
                        if (decoder.toTextureDecoder(mYUVTextures[0], mYUVTextures[1], mYUVTextures[2]) >= 0)
                        {
                            /*
                            Log.d(LOGTAG, "handleData:"
                                    + " " + lastCodec
                                    + " " + lastWidth + "x" + lastHeight
                                    + " " + avFrame.getFrmNo()
                                    + " " + session.decodeFrames.size()
                            );
                            */

                            if (mYUVImage == null)
                            {
                                mYUVImage = P2PVideoStillImage.create(lastWidth, lastHeight);
                            }
                            else
                            {
                                mYUVImage.updateSize(lastWidth, lastHeight);
                            }

                            session.surface.setStillImage(mYUVImage);
                            session.surface.requestRender();
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();

            decoder = null;
        }

        return true;
    }
}
