package de.xavaro.android.cam.util;

import android.annotation.SuppressLint;
import android.support.annotation.Nullable;

import android.content.SharedPreferences;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.cam.streams.VideoQuality;
import de.xavaro.android.cam.simple.Simple;
import de.xavaro.android.cam.simple.Json;

public class MP4Config
{
    private static final String LOGTAG = MP4Config.class.getSimpleName();

    private static final String PREFKEY = "cam.video.h264.modes";

    @Nullable
    public static MP4Config getConfigForVideoQuality(VideoQuality quality)
    {
        int twidth = quality.resX;
        int theight = quality.resY;
        int tfps = quality.framerate;

        Log.d(LOGTAG, "getConfigForVideoQuality: for "
                + " width=" + twidth
                + " theight=" + theight
                + " tfps=" + tfps
        );

        JSONArray configs = getConfigs();

        if (configs != null)
        {
            for (int inx = 0; inx < configs.length(); inx++)
            {
                JSONObject config = Json.getObject(configs, inx);
                if (config == null) continue;

                int width = Json.getInt(config, "width");
                int height = Json.getInt(config, "height");
                int fps = Json.getInt(config, "fps");

                if ((twidth == width) && (theight == height) && (tfps == fps))
                {
                    String sps = Json.getString(config, "SPS");
                    String pps = Json.getString(config, "PPS");
                    int cformat = Json.getInt(config, "cformat");

                    Log.d(LOGTAG, "getConfigForVideoQuality: got "
                            + " width=" + twidth
                            + " theight=" + theight
                            + " tfps=" + tfps
                            + " sps=" + sps
                            + " pps=" + pps
                            + " cformat=" + cformat
                    );

                    return new MP4Config(sps, pps, cformat);
                }
            }
        }

        Log.e(LOGTAG, "getConfigForVideoQuality: err "
                + " width=" + twidth
                + " theight=" + theight
                + " tfps=" + tfps
        );

        return null;
    }

    @Nullable
    public static JSONArray getConfigs()
    {
        SharedPreferences prefs = Simple.getPrefs();
        return Json.fromStringArray(prefs.getString(MP4Config.PREFKEY, null));
    }

    @SuppressLint("ApplySharedPref")
    public static void setConfigs(JSONArray configs)
    {
        SharedPreferences prefs = Simple.getPrefs();
        prefs.edit().putString(MP4Config.PREFKEY, Json.toPretty(configs)).commit();
    }

    private String mPPS;
    private String mSPS;
    private int cformat;

    public MP4Config(String sps, String pps, int cformat)
    {
        this.mPPS = sps;
        this.mSPS = pps;
        this.cformat = cformat;
    }

    public String getPFLB64()
    {
        byte[] bytes = getHexStringToBytes(mSPS);
        return Base64.encodeToString(bytes, 1, 3, Base64.NO_WRAP);
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

    public int getCFormat()
    {
        return cformat;
    }

    private static String getBytesToHexString(byte[] buffer, int start, int len)
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

    private static byte[] getHexStringToBytes(String hexstring)
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
}
