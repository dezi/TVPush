package de.xavaro.android.spr.simple;

import android.support.annotation.Nullable;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.speech.SpeechRecognizer;
import android.media.AudioManager;
import android.app.UiModeManager;
import android.app.Application;
import android.content.Context;
import android.util.Base64;
import android.os.Build;

import java.io.InputStream;

@SuppressWarnings("WeakerAccess")
public class Simple
{
    private static Resources resources;
    private static AudioManager audioManager;

    private static boolean istv;
    private static boolean issony;
    private static boolean isspeechin;

    public static void initialize(Application app)
    {
        resources = app.getResources();
        audioManager = (AudioManager) app.getSystemService(Context.AUDIO_SERVICE);

        UiModeManager uiModeManager = (UiModeManager) app.getSystemService(Context.UI_MODE_SERVICE);
        istv = (uiModeManager != null) && (uiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION);

        issony = istv && getDeviceModelName().startsWith("BRAVIA");

        isspeechin = SpeechRecognizer.isRecognitionAvailable(app);
    }

    public static boolean isTV()
    {
        return istv;
    }

    public static boolean isSony()
    {
        return issony;
    }

    public static boolean isSpeechIn()
    {
        return isspeechin;
    }

    public static String getDeviceModelName()
    {
        return Build.MODEL.toUpperCase();
    }

    public static void turnBeepOnOff(boolean on)
    {
        if ((audioManager != null))
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            {
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, on
                        ? AudioManager.ADJUST_UNMUTE : AudioManager.ADJUST_MUTE, 0);
            }
            else
            {
                audioManager.setStreamMute(AudioManager.STREAM_MUSIC, !on);
            }
        }
    }

    public static String getTrans(int resid, Object... args)
    {
        return String.format(resources.getString(resid), args);
    }

    @Nullable
    public static String getImageResourceBase64(int resid)
    {
        try
        {
            InputStream is = resources.openRawResource(+resid);
            byte[] buffer = new byte[16 * 1024];
            int xfer = is.read(buffer);
            is.close();

            return Base64.encodeToString(buffer, 0 ,xfer, Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
