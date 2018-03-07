package de.xavaro.android.simple;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Dump
{
    private static final String LOGTAG = Dump.class.getSimpleName();

    public static void dumpIntent(Intent intent)
    {
        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        for (String key : bundle.keySet())
        {
            Object value = bundle.get(key);

            Log.d(LOGTAG, "dumpIntent: key=" + key + " value=" + value);
        }
    }
}
