package de.xavaro.android.iot.base;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.security.Key;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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

    public static boolean equals(Float val1, Float val2)
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
        String str1 = (json1 == null) ? null : json1.toString();
        String str2 = (json2 == null) ? null : json2.toString();

        return ((str1 == null) && (str2 == null))
                || ((str1 != null) && (str2 != null) && str1.equals(str2));
    }

    public static boolean equals(JSONObject json1, JSONObject json2)
    {
        String str1 = (json1 == null) ? null : json1.toString();
        String str2 = (json2 == null) ? null : json2.toString();

        return ((str1 == null) && (str2 == null))
                || ((str1 != null) && (str2 != null) && str1.equals(str2));
    }

    @Nullable
    public static String hmacSha1UUID(String key, String data)
    {
        try
        {
            Key secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
            Mac instance = Mac.getInstance("HmacSHA1");
            instance.init(secretKeySpec);

            byte[] bytes = instance.doFinal(data.getBytes("UTF-8"));

            ByteBuffer bb = ByteBuffer.wrap(bytes,0 , 16);

            long high = bb.getLong();
            long low = bb.getLong();

            UUID uuid = new UUID(high, low);

            return uuid.toString();
        }
        catch (Exception ignore)
        {
        }

        return null;
    }
}
