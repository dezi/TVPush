package de.xavaro.android.iot.simple;

import android.support.annotation.Nullable;

import org.json.JSONArray;

import java.util.Map;

public class Prefs
{
    @Nullable
    public static String getString(String key)
    {
        try
        {
            return Simple.getPrefs().getString(key, null);
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static boolean setString(String key, String value)
    {
        return Simple.getPrefs().edit().putString(key, value).commit();
    }

    public static boolean removePref(String key)
    {
        return Simple.getPrefs().edit().remove(key).commit();
    }

    public static JSONArray searchPreferences(String prefix)
    {
        JSONArray result = new JSONArray();

        Map<String, ?> prefs = Simple.getPrefs().getAll();

        for (Map.Entry<String, ?> entry : prefs.entrySet())
        {
            if ((prefix != null) && !entry.getKey().startsWith(prefix)) continue;

            Json.put(result, entry.getKey());
        }

        return result;
    }
}
