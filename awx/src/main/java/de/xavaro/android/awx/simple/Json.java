package de.xavaro.android.awx.simple;

import android.support.annotation.Nullable;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"WeakerAccess"})
public class Json
{
    private static final String LOGTAG = Json.class.getSimpleName();

    @Nullable
    public static JSONObject fromStringObject(String jsonstr)
    {
        if (jsonstr != null)
        {
            try
            {
                return new JSONObject(jsonstr);
            }
            catch (Exception ex)
            {
                Log.d(LOGTAG, ex.toString());
            }
        }

        return null;
    }

    @Nullable
    public static JSONArray fromStringArray(String jsonstr)
    {
        if (jsonstr != null)
        {
            try
            {
                return new JSONArray(jsonstr);
            }
            catch (Exception ex)
            {
                Log.d(LOGTAG, ex.toString());
            }
        }

        return null;
    }

    public static JSONObject clone(JSONObject json)
    {
        try
        {
            return new JSONObject(json.toString());
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return new JSONObject();
    }

    public static JSONArray clone(JSONArray json)
    {
        try
        {
            return new JSONArray(json.toString());
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return new JSONArray();
    }

    public static void remove(JSONObject json, String key)
    {
        json.remove(key);
    }

    public static JSONArray removeFromArray(JSONArray json, int index)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            json.remove(index);

            return json;
        }
        else
        {
            //
            // For the sake of my HTC. For HTC not updating
            // their fucked phones to some reasonable
            // Android version...
            //

            JSONArray copy = new JSONArray();

            try
            {
                for (int inx = 0; inx < json.length(); inx++)
                {
                    if (inx != index) copy.put(json.get(inx));
                }
            }
            catch (Exception ignore)
            {
            }

            return copy;
        }
    }

    public static boolean equals(JSONObject j1, String k1, String val)
    {
        if ((k1 == null) && (val == null)) return true;

        String s1 = getString(j1, k1);

        return ((s1 == null) && (val == null)) || ((s1 != null) && s1.equals(val));
    }

    public static boolean equals(JSONObject j1, String k1, JSONObject j2)
    {
        Object s1 = get(j1, k1);
        Object s2 = get(j2, k1);

        return ((s1 == null) && (s2 == null)) || ((s1 != null) && s1.equals(s2));
    }

    public static void copy(JSONObject dst, String dkey, JSONObject src, String skey)
    {
        Object tmp = get(src, skey);
        if (tmp != null) put(dst, dkey, tmp);
    }

    public static void copy(JSONObject dst, String key, JSONObject src)
    {
        Object tmp = get(src, key);
        if (tmp != null) put(dst, key, tmp);
    }

    public static void copy(JSONObject dst, JSONObject src)
    {
        Iterator<String> keysIterator = src.keys();

        while (keysIterator.hasNext())
        {
            String key = keysIterator.next();

            copy(dst, key, src);
        }
    }

    public static JSONArray append(JSONArray dst, JSONArray src)
    {
        if ((dst != null) && (src != null))
        {
            for (int inx = 0; inx < src.length(); inx++)
            {
                put(dst, get(src, inx));
            }
        }

        return dst;
    }

    public static boolean has(JSONObject json, String key)
    {
        return (json != null) && json.has(key);
    }

    @SuppressWarnings("unchecked")
    public static void put(JSONObject json, String key, Object val)
    {
        try
        {
            if (val instanceof ArrayList)
            {
                JSONArray array = new JSONArray();

                ArrayList alist= (ArrayList) val;

                if ((!alist.isEmpty()) && (alist.get(0) instanceof String))
                {
                    for (String subval : (ArrayList<String>) val)
                    {
                        array.put(subval);
                    }
                }

                if ((!alist.isEmpty()) && (alist.get(0) instanceof Integer))
                {
                    for (Integer subval : (ArrayList<Integer>) val)
                    {
                        array.put(subval);
                    }
                }

                if ((!alist.isEmpty()) && (alist.get(0) instanceof Long))
                {
                    for (Long subval : (ArrayList<Long>) val)
                    {
                        array.put(subval);
                    }
                }

                if ((!alist.isEmpty()) && (alist.get(0) instanceof Boolean))
                {
                    for (Boolean subval : (ArrayList<Boolean>) val)
                    {
                        array.put(subval);
                    }
                }

                json.put(key, array);
            }
            else
            {
                json.put(key, val);
            }
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }
    }

    public static void put(JSONArray json, Object val)
    {
        json.put(val);
    }

    @Nullable
    public static Object get(JSONObject json, String key)
    {
        try
        {
            return json.get(key);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static Object get(JSONArray json, int index)
    {
        try
        {
            return json.get(index);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    public static float getFloat(JSONObject json, String key)
    {
        try
        {
            return (float) json.getDouble(key);
        }
        catch (Exception ignore)
        {
        }

        return 0.0f;
    }

    public static double getDouble(JSONObject json, String key)
    {
        try
        {
            return json.getDouble(key);
        }
        catch (Exception ignore)
        {
        }

        return 0.0;
    }

    public static boolean getBoolean(JSONObject json, String key)
    {
        try
        {
            return json.getBoolean(key);
        }
        catch (Exception ignore)
        {
        }

        return false;
    }

    public static int getInt(JSONObject json, String key)
    {
        try
        {
            return json.getInt(key);
        }
        catch (Exception ignore)
        {
        }

        return 0;
    }

    public static long getLong(JSONObject json, String key)
    {
        try
        {
            return json.getLong(key);
        }
        catch (Exception ignore)
        {
        }

        return 0;
    }

    @Nullable
    public static String getString(JSONObject json, String key)
    {
        try
        {
            Object obj = json.get(key);

            if (obj instanceof String) return (String) obj;

            if (obj instanceof JSONArray)
            {
                JSONArray strings = json.getJSONArray(key);

                String result = "";

                for (int inx = 0; inx < strings.length(); inx++)
                {
                    result += getString(strings, inx);
                }

                return result;
            }
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    public static int getInt(JSONArray json, int index)
    {
        try
        {
            return json.getInt(index);
        }
        catch (Exception ignore)
        {
        }

        return 0;
    }

    @Nullable
    public static String getString(JSONArray json, int index)
    {
        try
        {
            return json.getString(index);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static Object getGeneric(JSONObject json, String key)
    {
        try
        {
            return json.get(key);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }


    @Nullable
    public static JSONArray getArray(JSONObject json, String key)
    {
        try
        {
            return json.getJSONArray(key);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static JSONObject getObject(JSONObject json, String key)
    {
        try
        {
            return json.getJSONObject(key);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    @Nullable
    public static JSONObject getObject(JSONArray json, int index)
    {
        try
        {
            return json.getJSONObject(index);
        }
        catch (Exception ignore)
        {
        }

        return null;
    }

    public static String toJavaScript(JSONObject jsonObject)
    {
        return (jsonObject == null) ? "{}" : Json.toPretty(jsonObject);
    }

    public static String toJavaScript(JSONArray jsonArray)
    {
        return (jsonArray == null) ? "[]" : Json.toPretty(jsonArray);
    }

    public static void makeFormat(JSONObject jsonObject, String key, Object... args)
    {
        if (jsonObject != null)
        {
            String value = getString(jsonObject, key);

            if (value != null)
            {
                value = String.format(value, args);

                put(jsonObject, key, value);
            }
        }
    }

    public static String toPretty(JSONObject jsonObject)
    {
        if (jsonObject != null)
        {
            try
            {
                return defuck(jsonObject.toString(2));
            }
            catch (Exception ignored)
            {
            }
        }

        return "NULL";
    }

    public static String toPretty(JSONArray jsonArray)
    {
        if (jsonArray != null)
        {
            try
            {
                return defuck(jsonArray.toString(2));
            }
            catch (Exception ignored)
            {
            }
        }

        return "NULL";
    }

    public static Set<String> toSet(JSONArray jsonArray)
    {
        Set<String> set = new HashSet<>();

        if (jsonArray != null)
        {
            try
            {
                for (int inx = 0; inx < jsonArray.length(); inx++)
                {
                    set.add(jsonArray.getString(inx));
                }
            }
            catch (Exception ignored)
            {
            }
        }

        return set;
    }

    public static String defuck(String json)
    {
        //
        // I hate slash escaping.
        //

        return (json == null) ? "{}" : json.replace("\\/","/");
    }

    public static JSONArray sort(JSONArray array, String field, boolean descending)
    {
        final String sort = field;
        final boolean desc = descending;

        class comparedat implements Comparator<JSONObject>
        {
            public int compare(JSONObject a, JSONObject b)
            {
                String astr = desc ? getString(b, sort) : getString(a, sort);
                String bstr = desc ? getString(a, sort) : getString(b, sort);

                return ((astr != null) && (bstr != null)) ? astr.compareTo(bstr) : 0;
            }
        }

        List<JSONObject> jsonValues = new ArrayList<>();

        for (int inx = 0; inx < array.length(); inx++)
        {
            jsonValues.add(getObject(array, inx));
        }

        Collections.sort(jsonValues, new comparedat());

        return new JSONArray(jsonValues);
    }

    public static JSONArray sortInteger(JSONArray array, String field, boolean descending)
    {
        final String sort = field;
        final boolean desc = descending;

        class comparedat implements Comparator<JSONObject>
        {
            public int compare(JSONObject a, JSONObject b)
            {
                int aval = desc ? getInt(b, sort) : getInt(a, sort);
                int bval = desc ? getInt(a, sort) : getInt(b, sort);

                return aval - bval;
            }
        }

        List<JSONObject> jsonValues = new ArrayList<>();

        for (int inx = 0; inx < array.length(); inx++)
            jsonValues.add(getObject(array, inx));

        Collections.sort(jsonValues, new comparedat());

        return new JSONArray(jsonValues);
    }

    public static void fixUpNumber2String(JSONObject obj, String key)
    {
        if (has(obj, key) && (getString(obj, key) == null))
        {
            long val = getLong(obj, key);

            put(obj, key, String.valueOf(val));
        }
    }
}
