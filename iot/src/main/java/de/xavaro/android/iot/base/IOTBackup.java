package de.xavaro.android.iot.base;

import android.util.Log;

import org.json.JSONArray;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Prefs;

public class IOTBackup
{
    private final static String LOGTAG = IOTBackup.class.getSimpleName();

    public static void saveBackup()
    {
        JSONArray keys = Prefs.searchPreferences("iot.");

        keys = Json.sort(keys, false);

        for (int inx = 0; inx < keys.length(); inx++)
        {
            String iot = Json.getString(keys, inx);
            String json = Prefs.getString(iot);

            Log.d(LOGTAG, "saveBackup: iot=" + iot);
        }
    }

    public static void restoreBackup()
    {

    }
}
