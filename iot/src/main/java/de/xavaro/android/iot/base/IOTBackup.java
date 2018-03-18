package de.xavaro.android.iot.base;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Prefs;
import de.xavaro.android.iot.simple.Simple;

public class IOTBackup
{
    private final static String LOGTAG = IOTBackup.class.getSimpleName();

    public static void saveBackup(Context context)
    {
        if (Simple.checkStoragePermission(context))
        {
            JSONArray keys = Json.sort(Prefs.searchPreferences("iot."), false);

            JSONObject dump = new JSONObject();

            for (int inx = 0; inx < keys.length(); inx++)
            {
                String iot = Json.getString(keys, inx);
                String json = Prefs.getString(iot);

                Json.put(dump, iot, Json.fromStringObject(json));

                Log.d(LOGTAG, "saveBackup: iot=" + iot);
            }

            File external = Environment.getExternalStorageDirectory();
            File backupJSON = new File(external, "iot.backup.json");

            Simple.writeTextFile(backupJSON, Json.toPretty(dump));
        }
    }

    public static void restoreBackup()
    {

    }
}
