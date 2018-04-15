package de.xavaro.android.cam.util;

import android.util.Base64;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CAMMP4Config
{
    private static final String LOGTAG = CAMMP4Config.class.getSimpleName();

    private CAMMP4Parser mp4Parser;
    private String mProfilLevel, mPPS, mSPS;

    public CAMMP4Config(String profil, String sps, String pps)
    {
        mProfilLevel = profil;
        mPPS = pps;
        mSPS = sps;
    }

    public CAMMP4Config(String sps, String pps)
    {
        mPPS = pps;
        mSPS = sps;
        mProfilLevel = CAMMP4Parser.toHexString(Base64.decode(sps, Base64.NO_WRAP), 1, 3);
    }

    public CAMMP4Config(byte[] sps, byte[] pps)
    {
        mPPS = Base64.encodeToString(pps, 0, pps.length, Base64.NO_WRAP);
        mSPS = Base64.encodeToString(sps, 0, sps.length, Base64.NO_WRAP);
        mProfilLevel = CAMMP4Parser.toHexString(sps, 1, 3);
    }

    /**
     * Finds SPS & PPS parameters inside a .mp4.
     *
     * @param path Path to the file to analyze
     * @throws IOException
     * @throws FileNotFoundException
     */
    public CAMMP4Config(String path) throws IOException, FileNotFoundException
    {

        CAMStsdBox stsdBox;

        // We open the mp4 file and parse it
        try
        {
            mp4Parser = CAMMP4Parser.parse(path);
        }
        catch (IOException ignore)
        {
            // Maybe enough of the file has been parsed and we can get the stsd box
        }

        // We find the stsdBox
        stsdBox = mp4Parser.getStsdBox();
        mPPS = stsdBox.getHEXPPS();
        mSPS = stsdBox.getHEXSPS();
        mProfilLevel = stsdBox.getProfileLevel();

        mp4Parser.close();

    }

    public String getProfileLevel()
    {
        return mProfilLevel;
    }

    public String getHEXPPS()
    {
        Log.d(LOGTAG, "PPS: " + mPPS);
        return mPPS;
    }

    public String getHEXSPS()
    {
        Log.d(LOGTAG, "SPS: " + mSPS);
        return mSPS;
    }

}
