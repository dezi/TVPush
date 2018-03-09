package de.xavaro.android.iot.things;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.UUID;

import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Prefs;

@SuppressWarnings("WeakerAccess")
public abstract class IOTBase
{
    private final static String LOGTAG = IOTBase.class.getSimpleName();

    public String uuid;

    public IOTBase()
    {
        uuid = UUID.randomUUID().toString();
    }

    public IOTBase(String uuid)
    {
        this.uuid = uuid;
    }

    public IOTBase(String jsonstr, boolean dummy)
    {
        fromJsonString(jsonstr);
    }

    public IOTBase(JSONObject json)
    {
        fromJson(json);
    }

    public String toJsonString()
    {
        return Json.toPretty(toJson());
    }

    public boolean fromJsonString(String json)
    {
        return fromJson(Json.fromStringObject(json));
    }

    public JSONObject toJson()
    {
        JSONObject json = new JSONObject();

        for (Field field : getClass().getFields())
        {
            try
            {
                int modifiers = field.getModifiers();

                if ((modifiers & Modifier.FINAL) == Modifier.FINAL) continue;
                if ((modifiers & Modifier.STATIC) == Modifier.STATIC) continue;
                if ((modifiers & Modifier.PUBLIC) != Modifier.PUBLIC) continue;

                Object ival = field.get(this);
                if (ival == null) continue;

                if ((ival instanceof JSONObject)
                        || (ival instanceof JSONArray)
                        || (ival instanceof ArrayList)
                        || (ival instanceof Integer)
                        || (ival instanceof Boolean)
                        || (ival instanceof String)
                        || (ival instanceof Byte)
                        || (ival instanceof Long))
                {
                    String name = field.getName();

                    Json.put(json, name, ival);
                }
            }
            catch (Exception ignore)
            {
            }
        }

        return json;
    }

    public boolean fromJson(JSONObject json)
    {
        if (json == null) return false;

        boolean ok = false;

        for (Field field : getClass().getFields())
        {
            try
            {
                String name = field.getName();
                int modifiers = field.getModifiers();

                if ((modifiers & Modifier.FINAL) == Modifier.FINAL) continue;
                if ((modifiers & Modifier.STATIC) == Modifier.STATIC) continue;
                if ((modifiers & Modifier.PUBLIC) != Modifier.PUBLIC) continue;

                if (Json.has(json, name))
                {
                    Object jval = Json.get(json, name);

                    try
                    {
                        field.set(this, jval);
                        ok = true;
                    }
                    catch (Exception ignore)
                    {
                        //
                        // Someone changed the data type in between.
                        //
                    }

                    Json.remove(json, name);
                }
            }
            catch (Exception ignore)
            {
                return false;
            }
        }

        String uncomsumed = json.toString();

        if (! uncomsumed.equals("{}"))
        {
            Log.d(LOGTAG, "fromJson: uncomsumed=" + uncomsumed);
        }

        return ok;
    }

    public String getClassKey()
    {
        return "iot." + getClass().getSimpleName() + ".";
    }

    public String getUUIDKey()
    {
        return getClassKey() + uuid;
    }

    public boolean saveToStorage()
    {
        boolean ok = false;

        if ((uuid != null) && ! uuid.isEmpty())
        {
            String key = getUUIDKey();
            String json = toJsonString();

            ok = Prefs.setString(key, json);

            Log.d(LOGTAG, "saveToStorage: key=" + key + " ok=" + ok + " json=" + ((json == null) ? "null" : "ok"));

            //Log.d(LOGTAG, json);
        }

        return ok;
    }

    public boolean loadFromStorage()
    {
        String key = getUUIDKey();
        String json = Prefs.getString(key);

        boolean ok = fromJsonString(json);

        Log.d(LOGTAG, "loadFromStorage: key=" + key + " ok=" + ok + " json=" + ((json == null) ? "null" : "ok"));

        //if (json != null) Log.d(LOGTAG, json);

        return ok;
    }
}
