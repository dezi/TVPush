package de.xavaro.android.cam.util;

import android.support.annotation.Nullable;
import android.util.Base64;

import de.xavaro.android.cam.streams.VideoQuality;

public class MP4Config
{
    public final static String TAG = "MP4Config";

    private String mPFL;
    private String mPPS;
    private String mSPS;

    public MP4Config(byte[] sps, byte[] pps)
    {
        mPPS = toHexString(pps, 0, pps.length);
        mSPS = toHexString(sps, 0, sps.length);
        mPFL = toHexString(sps, 1, 3);
    }

    public String getPFLB64()
    {
        byte[] bytes = getHexStringToBytes(mPFL);
        return Base64.encodeToString(bytes, 0, bytes.length, Base64.NO_WRAP);
    }

    public byte[] getPPS()
    {
        return getHexStringToBytes(mPPS);
    }

    public String getPPSB64()
    {
        byte[] bytes = getHexStringToBytes(mPPS);
        return Base64.encodeToString(bytes, 0, bytes.length, Base64.NO_WRAP);
    }


    public byte[] getSPS()
    {
        return getHexStringToBytes(mSPS);
    }

    public String getSPSB64()
    {
        byte[] bytes = getHexStringToBytes(mSPS);
        return Base64.encodeToString(bytes, 0, bytes.length, Base64.NO_WRAP);
    }

    private static String toHexString(byte[] buffer, int start, int len)
    {
        String c;
        StringBuilder s = new StringBuilder();
        for (int i = start; i < start + len; i++)
        {
            c = Integer.toHexString(buffer[i] & 0xFF);
            s.append(c.length() < 2 ? "0" + c : c);
        }
        return s.toString();
    }

    public static byte[] getHexStringToBytes(String hexstring)
    {
        if (hexstring == null) return null;

        hexstring = hexstring.replace(" ", "");

        byte[] bytes = new byte[ hexstring.length() >> 1 ];

        for (int inx = 0; inx < hexstring.length(); inx += 2)
        {
            //noinspection PointlessBitwiseExpression,PointlessArithmeticExpression
            bytes[ inx >> 1 ] = (byte)
                    ((Character.digit(hexstring.charAt(inx + 0), 16) << 4)
                            + Character.digit(hexstring.charAt(inx + 1), 16) << 0);
        }

        return bytes;
    }

    @Nullable
    public static MP4Config getConfigForVideoQuality(VideoQuality quality)
    {
        return null;
    }
}
