package de.xavaro.android.iot.base;

import org.json.JSONArray;

import de.xavaro.android.simple.Json;

public class IOTSimple
{
    public static boolean equals(String str1, String str2)
    {
        return ((str1 == null) && (str2 == null))
                || ((str1 != null) && (str2 != null) && str1.equals(str2));
    }

    public static boolean nequals(String str1, String str2)
    {
        return ! equals(str1, str2);
    }

    public static boolean equals(JSONArray json1, JSONArray json2)
    {
        String str1 = Json.toPretty(json1);
        String str2 = Json.toPretty(json2);

        return ((str1 == null) && (str2 == null))
                || ((str1 != null) && (str2 != null) && str1.equals(str2));
    }

    public static boolean nequals(JSONArray json1, JSONArray json2)
    {
        return ! equals(json1, json2);
    }
}
