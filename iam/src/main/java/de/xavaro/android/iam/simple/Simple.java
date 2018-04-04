package de.xavaro.android.iam.simple;

import android.support.annotation.Nullable;

import android.content.res.Resources;
import android.app.Application;
import android.util.Base64;

import java.io.InputStream;

public class Simple
{
    private static Resources resources;

    public static void initialize(Application app)
    {
        resources = app.getResources();
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

            return Base64.encodeToString(buffer, 0 ,xfer, android.util.Base64.NO_WRAP);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
