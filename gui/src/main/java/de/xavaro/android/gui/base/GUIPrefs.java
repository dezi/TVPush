package de.xavaro.android.gui.base;

import org.json.JSONObject;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;

public class GUIPrefs
{
    public static JSONObject readPref(String key)
    {
        String prefkey = "gui." + key;
        String jsonpref = Simple.getPrefs().getString(prefkey, null);
        JSONObject pref = Json.fromStringObject(jsonpref);

        return (pref != null) ? pref : new JSONObject();
    }

    public static void savePref(String key, JSONObject pref)
    {
        String prefkey = "gui.pref." + key;
        Simple.getPrefs().edit().putString(prefkey, Json.toPretty(pref)).apply();
    }
}
