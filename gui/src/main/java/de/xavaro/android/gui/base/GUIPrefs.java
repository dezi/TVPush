package de.xavaro.android.gui.base;

import android.annotation.SuppressLint;

import org.json.JSONObject;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.simple.Simple;

public class GUIPrefs
{
    private final static String LOGTAG = GUIPrefs.class.getSimpleName();

    public static JSONObject readPref(String key)
    {
        String prefkey = "gui.pref." + key;
        String jsonpref = Simple.getPrefs().getString(prefkey, null);
        JSONObject pref = Json.fromStringObject(jsonpref);

        return (pref != null) ? pref : new JSONObject();
    }

    @SuppressLint("ApplySharedPref")
    public static void savePref(String key, JSONObject pref)
    {
        String prefkey = "gui.pref." + key;
        Simple.getPrefs().edit().putString(prefkey, Json.toPretty(pref)).commit();

        Log.d(LOGTAG, "savePref: prefkey=" + prefkey + " json=" + pref.toString());
    }
}
