package de.xavaro.android.iot.base;

import org.json.JSONArray;
import org.json.JSONObject;

public class IOTSimple
{
    public static boolean equals(String str1, String str2)
    {
        return ((str1 == null) && (str2 == null))
                || ((str1 != null) && (str2 != null) && str1.equals(str2));
    }

    public static boolean equals(Long val1, Long val2)
    {
        return ((val1 == null) && (val2 == null))
                || ((val1 != null) && (val2 != null) && val1.equals(val2));
    }

    public static boolean equals(Double val1, Double val2)
    {
        return ((val1 == null) && (val2 == null))
                || ((val1 != null) && (val2 != null) && val1.equals(val2));
    }

    public static boolean equals(Integer val1, Integer val2)
    {
        return ((val1 == null) && (val2 == null))
                || ((val1 != null) && (val2 != null) && val1.equals(val2));
    }

    public static boolean equals(JSONArray json1, JSONArray json2)
    {
        String str1 = json1.toString();
        String str2 = json2.toString();

        return ((str1 == null) && (str2 == null))
                || ((str1 != null) && (str2 != null) && str1.equals(str2));
    }

    public static boolean equals(JSONObject json1, JSONObject json2)
    {
        String str1 = json1.toString();
        String str2 = json2.toString();

        return ((str1 == null) && (str2 == null))
                || ((str1 != null) && (str2 != null) && str1.equals(str2));
    }
}
