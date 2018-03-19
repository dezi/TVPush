package de.xavaro.android.iot.base;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.UUID;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Prefs;

@SuppressWarnings("WeakerAccess")
public abstract class IOTObject
{
    private final static String LOGTAG = IOTObject.class.getSimpleName();

    protected boolean changed;
    protected boolean changedSys;
    protected boolean changedUsr;

    public String uuid;
    public Long sts;
    public Long uts;

    public IOTObject()
    {
        uuid = UUID.randomUUID().toString();
    }

    public IOTObject(String uuid)
    {
        this.uuid = uuid;

        loadFromStorage();
    }

    public IOTObject(String jsonstr, boolean dummy)
    {
        fromJsonString(jsonstr);
    }

    public IOTObject(JSONObject json)
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
                String name = field.getName();
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
                        || (ival instanceof Double)
                        || (ival instanceof Float)
                        || (ival instanceof Byte)
                        || (ival instanceof Long))
                {
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
                int modifiers = field.getModifiers();

                if ((modifiers & Modifier.FINAL) == Modifier.FINAL) continue;
                if ((modifiers & Modifier.STATIC) == Modifier.STATIC) continue;
                if ((modifiers & Modifier.PUBLIC) != Modifier.PUBLIC) continue;

                String name = field.getName();
                String type = field.getType().getName();

                if (Json.has(json, name))
                {
                    Object jval = Json.get(json, name);

                    if (type.equals("java.lang.Long")) jval =  Json.getLong(json, name);
                    if (type.equals("java.lang.Float")) jval =  Json.getFloat(json, name);
                    if (type.equals("java.lang.Double")) jval =  Json.getDouble(json, name);

                    try
                    {
                        field.set(this, jval);
                        ok = true;
                    }
                    catch (Exception ex)
                    {
                        //
                        // Someone changed the data type in between
                        // or supplied wrong data type.
                        //

                        ex.printStackTrace();
                    }
                }
            }
            catch (Exception ignore)
            {
                return false;
            }
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

    public boolean nequals(String str1, String str2)
    {
        if (! IOTSimple.equals(str1, str2))
        {
            return changed = true;
        }

        return false;
    }

    public boolean nequals(Long val1, Long val2)
    {
        if (! IOTSimple.equals(val1, val2))
        {
            return changed = true;
        }

        return false;
    }

    public boolean nequals(Float val1, Float val2)
    {
        if (! IOTSimple.equals(val1, val2))
        {
            return changed = true;
        }

        return false;
    }

    public boolean nequals(Double val1, Double val2)
    {
        if (! IOTSimple.equals(val1, val2))
        {
            return changed = true;
        }

        return false;
    }

    public boolean nequals(Integer val1, Integer val2)
    {
        if (! IOTSimple.equals(val1, val2))
        {
            return changed = true;
        }

        return false;
    }

    public boolean nequals(JSONArray json1, JSONArray json2)
    {
        if (! IOTSimple.equals(json1, json2))
        {
            return changed = true;
        }

        return false;
    }

    public boolean nequals(JSONObject json1, JSONObject json2)
    {
        if (! IOTSimple.equals(json1, json2))
        {
            return changed = true;
        }

        return false;
    }

    public void saveIfChanged()
    {
        if (changedSys || (sts == null) || (sts == 0))
        {
            sts = System.currentTimeMillis();
        }

        if (changedUsr || (uts == null) || (uts == 0))
        {
            uts = System.currentTimeMillis();
        }

        if (changedSys || changedUsr)
        {
            saveToStorage();
        }

        changed = false;
        changedSys = false;
        changedUsr = false;
    }
}
