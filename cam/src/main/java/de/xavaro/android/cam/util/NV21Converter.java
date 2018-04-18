package de.xavaro.android.cam.util;

import android.media.MediaCodecInfo;
import android.util.Log;

import java.nio.ByteBuffer;

public class NV21Converter
{
    private static final String LOGTAG = NV21Converter.class.getSimpleName();

    public final static int[] formatsWeLike = new int[]
            {
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar,
                    MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar,
                    MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar
            };

    private int mSliceHeight, mHeight;
    private int mStride, mWidth;
    private int mSize;
    private boolean mPlanar, mPanesReversed = false;
    private int mYPadding;
    private byte[] mBuffer;

    public NV21Converter(int width, int height, int colorFormat)
    {
        setSize(width, height);
        setEncoderColorFormat(colorFormat);
        setColorPanesReversed(false);
    }

    public void setSize(int width, int height)
    {
        mHeight = height;
        mWidth = width;
        mSliceHeight = height;
        mStride = width;
        mSize = mWidth * mHeight;
    }

    public void setEncoderColorFormat(int colorFormat)
    {
        switch (colorFormat)
        {
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar:
            case MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar:
                setPlanar(false);
                break;
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar:
            case MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar:
                setPlanar(true);
                break;
        }
    }

    public void setColorPanesReversed(boolean b)
    {
        mPanesReversed = b;
    }

    public void setStride(int width)
    {
        mStride = width;
    }

    public void setSliceHeigth(int height)
    {
        mSliceHeight = height;
    }

    public void setPlanar(boolean planar)
    {
        mPlanar = planar;
    }

    public void setYPadding(int padding)
    {
        mYPadding = padding;
    }

    public int getBufferSize()
    {
        return 3 * mSize / 2;
    }

    public int getStride()
    {
        return mStride;
    }

    public int getSliceHeigth()
    {
        return mSliceHeight;
    }

    public int getYPadding()
    {
        return mYPadding;
    }

    public boolean getPlanar()
    {
        return mPlanar;
    }

    public boolean getUVPanesReversed()
    {
        return mPanesReversed;
    }

    public void convert(byte[] data, ByteBuffer buffer)
    {
        byte[] result = convert(data);
        int min = buffer.capacity() < data.length ? buffer.capacity() : data.length;
        buffer.put(result, 0, min);
    }

    public byte[] convert(byte[] data)
    {
        Log.d(LOGTAG, "convert:"
                + " data=" + data.length
                + " planar=" + mPlanar
                + " mSliceHeight=" + mSliceHeight + ":" + mHeight
                + " mStride=" + mStride + ":" + mWidth
        );

        if ((mSliceHeight != mHeight) || (mStride != mWidth))
        {
            return data;
        }

        if (mBuffer == null || mBuffer.length != 3 * mSliceHeight * mStride / 2 + mYPadding)
        {
            mBuffer = new byte[3 * mSliceHeight * mStride / 2 + mYPadding];
        }

        if (! mPlanar)
        {
            if (! mPanesReversed)
            {
                int end = mSize + mSize / 2;
                byte tmp;

                for (int inx = mSize; inx < end; inx += 2)
                {
                    tmp = data[inx + 1];
                    data[inx + 1] = data[inx];
                    data[inx] = tmp;
                }
            }

            if (mYPadding == 0) return data;

            System.arraycopy(data, 0, mBuffer, 0, mSize);
            System.arraycopy(data, mSize, mBuffer, mSize + mYPadding, mSize / 2);

            return mBuffer;
        }
        else
        {
            if (!mPanesReversed)
            {
                for (int i = 0; i < mSize / 4; i += 1)
                {
                    mBuffer[i] = data[mSize + 2 * i + 1];
                    mBuffer[mSize / 4 + i] = data[mSize + 2 * i];
                }
            }
            else
            {
                for (int i = 0; i < mSize / 4; i += 1)
                {
                    mBuffer[i] = data[mSize + 2 * i];
                    mBuffer[mSize / 4 + i] = data[mSize + 2 * i + 1];
                }
            }

            if (mYPadding == 0)
            {
                System.arraycopy(mBuffer, 0, data, mSize, mSize / 2);

                return data;
            }
            else
            {
                System.arraycopy(data, 0, mBuffer, 0, mSize);
                System.arraycopy(mBuffer, 0, mBuffer, mSize + mYPadding, mSize / 2);

                return mBuffer;
            }
        }
    }

    public byte[] createTestImage()
    {
        byte[] mInitialImage = new byte[3 * mSize / 2];

        for (int i = 0; i < mSize; i++)
        {
            mInitialImage[i] = (byte) (40 + i % 199);
        }
        for (int i = mSize; i < 3 * mSize / 2; i += 2)
        {
            mInitialImage[i] = (byte) (40 + i % 200);
            mInitialImage[i + 1] = (byte) (40 + (i + 99) % 200);
        }

        return mInitialImage;
    }
}
