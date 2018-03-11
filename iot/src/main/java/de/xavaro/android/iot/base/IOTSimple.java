package de.xavaro.android.iot.base;

import org.json.JSONArray;

import java.util.UUID;

import de.xavaro.android.iot.simple.Json;

public class IOTSimple
{
    public static boolean isDefined(Object val)
    {
        return (val != null);
    }

    public static boolean isDefined(String val)
    {
        return ((val != null) && ! val.isEmpty());
    }

    public static boolean isDefined(boolean val)
    {
        return true;
    }

    public static boolean isDefined(int val)
    {
        return (val >= 0);
    }

    public static boolean isDefined(long val)
    {
        return (val >= 0);
    }

    public static boolean isBetter(String val1, String val2)
    {
        return isDefined(val1) && nequals(val1, val2);
    }

    public static boolean isBetter(int val1, int val2)
    {
        return isDefined(val1) && (val1 != val2);
    }

    public static boolean isBetter(long val1, long val2)
    {
        return isDefined(val1) && (val1 != val2);
    }

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

    public static String s()
    {
        return UUID.randomUUID().toString();
    }
}
