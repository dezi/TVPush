package com.p2p.p2pcamera;

import android.util.Log;
import javax.microedition.khronos.egl.EGL10;

import com.decoder.util.H264Decoder;

public class P2PReaderThreadH264Dec extends Thread
{
    private static final String LOGTAG = P2PReaderThreadH264Dec.class.getSimpleName();

    static final int MAX_FRAMEBUF = 4000000;

    private P2PSession session;
    private H264Decoder decoder;
    private boolean haveIFrame;
    private byte[] yuvbuf;
    private int lastWidth;
    private int lastHeight;

    public P2PReaderThreadH264Dec(P2PSession session)
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
        lastWidth = 0;
        lastHeight = 0;

        decoder = new H264Decoder(0);
        yuvbuf = new byte[MAX_FRAMEBUF];

        Log.d(LOGTAG, "onStart: done.");

        return true;
    }

    public boolean onStop()
    {
        decoder.nativeDestroy();
        decoder = null;
        yuvbuf = null;

        Log.d(LOGTAG, "onStop: done.");

        return true;
    }

    public boolean handleData(P2PAVFrame aVFrame)
    {
        try
        {
            if ((decoder != null) && aVFrame.isIFrame() || haveIFrame)
            {
                haveIFrame = true;

                /*
                Log.d(LOGTAG, "handleData:"
                        + " width=" + aVFrame.getVideoWidth()
                        + " height=" + aVFrame.getVideoHeight()
                        + " size=" + aVFrame.getFrmSize());
                */

                if (((lastWidth != 0) || (lastHeight != 0)) &&
                        ((lastWidth != aVFrame.getVideoWidth()) || (lastHeight != aVFrame.getVideoHeight())))
                {
                    decoder.nativeDestroy();
                    decoder = new H264Decoder(0);

                    Log.d(LOGTAG, "handleData: decoder recreated...");

                    haveIFrame = aVFrame.isIFrame();
                }

                lastWidth = aVFrame.getVideoWidth();
                lastHeight = aVFrame.getVideoHeight();

                if (haveIFrame)
                {
                    if (decoder.consumeNalUnitsFromDirectBuffer(aVFrame.frmData, aVFrame.getFrmSize(), (long) aVFrame.getFrmNo()) > 0)
                    {
                        int width = decoder.getWidth();
                        int height = decoder.getHeight();
                        int size = decoder.getOutputByteSize();

                        Log.d(LOGTAG, "handleData: "
                                + width + "x" + height
                                + " " + aVFrame.getFrmNo()
                                + " " + size
                                + " " + decoder.isFrameReady()
                                + " " + session.decodeFrames.size()
                        );

                        if (decoder.isFrameReady() && (size <= yuvbuf.length) && ((aVFrame.getFrmNo() % 2) == 1))
                        {
                            long xfer = decoder.getYUVData(yuvbuf, yuvbuf.length);

                            if (xfer != -1)
                            {
                                //session.surface.mRenderer.setFrame(this.yuvbuf, width, height);
                            }
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

    private byte[] yuv2rgb(byte[] yuyv_image, int width, int height)
    {
        byte[] rgb_image = new byte[width * height * 3];

        int y;
        int cr;
        int cb;

        double r;
        double g;
        double b;

        int i, j;

        for (i = 0, j = 0; i < width * height * 3; i += 6, j += 4)
        {
            y = yuyv_image[j];
            cb = yuyv_image[j + 1];
            cr = yuyv_image[j + 3];

            r = y + (1.4065 * (cr - 128));
            g = y - (0.3455 * (cb - 128)) - (0.7169 * (cr - 128));
            b = y + (1.7790 * (cb - 128));

            //This prevents colour distortions in your rgb image
            if (r < 0) r = 0;
            else
                if (r > 255) r = 255;
            if (g < 0) g = 0;
            else
                if (g > 255) g = 255;
            if (b < 0) b = 0;
            else
                if (b > 255) b = 255;

            rgb_image[i] = (byte) r;
            rgb_image[i + 1] = (byte) g;
            rgb_image[i + 2] = (byte) b;

            //second pixel
            y = yuyv_image[j + 2];
            cb = yuyv_image[j + 1];
            cr = yuyv_image[j + 3];

            r = y + (1.4065 * (cr - 128));
            g = y - (0.3455 * (cb - 128)) - (0.7169 * (cr - 128));
            b = y + (1.7790 * (cb - 128));

            if (r < 0) r = 0;
            else
                if (r > 255) r = 255;
            if (g < 0) g = 0;
            else
                if (g > 255) g = 255;
            if (b < 0) b = 0;
            else
                if (b > 255) b = 255;

            rgb_image[i + 3] = (byte) r;
            rgb_image[i + 4] = (byte) g;
            rgb_image[i + 5] = (byte) b;
        }

        return rgb_image;
    }
}
