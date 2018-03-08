package de.xavaro.android.iot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;

import zz.top.utl.Json;

public abstract class IOTBase
{
    private final static String LOGTAG = IOTBase.class.getSimpleName();

    public String uuid;
    public String name;

    public IOTBase()
    {
    }

    public IOTBase(String uuid)
    {
        this.uuid = uuid;
    }

    public IOTBase(String uuid, String name)
    {
        this.uuid = uuid;
        this.name = name;
    }

    public IOTBase(JSONObject json)
    {
        fromJson(json);
    }

    public IOTBase(String json, boolean dummy)
    {
        fromJsonString(json);
    }

    public String toJsonString()
    {
        return Json.toPretty(toJson());
    }

    public void fromJsonString(String json)
    {
        fromJson(Json.fromStringObject(json));
    }

    public JSONObject toJson()
    {
        JSONObject json = new JSONObject();

        for (Field field : getClass().getDeclaredFields())
        {
            try
            {
                int modifier = field.getModifiers();

                if ((modifier & Modifier.PUBLIC) != Modifier.PUBLIC) continue;
                if ((modifier & Modifier.STATIC) == Modifier.STATIC) continue;

                Object ival = field.get(this);

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

    public void fromJson(JSONObject json)
    {
        for (Field field : getClass().getDeclaredFields())
        {
            try
            {
                int modifier = field.getModifiers();

                if ((modifier & Modifier.PUBLIC) != Modifier.PUBLIC) continue;
                if ((modifier & Modifier.STATIC) == Modifier.STATIC) continue;

                String name = field.getName();

                if (Json.has(json, name))
                {
                    Object jval = Json.get(json, name);
                    Object ival = field.get(this);

                    if (((jval instanceof JSONObject) && (ival instanceof JSONObject))
                        || ((jval instanceof JSONArray) && (ival instanceof JSONArray))
                        || ((jval instanceof ArrayList) && (ival instanceof ArrayList))
                        || ((jval instanceof Integer) && (ival instanceof Integer))
                        || ((jval instanceof Boolean) && (ival instanceof Boolean))
                        || ((jval instanceof String) && (ival instanceof String))
                        || ((jval instanceof Byte) && (ival instanceof Byte))
                        || ((jval instanceof Long) && (ival instanceof Long)))

                    {
                        field.set(this, jval);
                    }
                }
            }
            catch (Exception ignore)
            {
            }
        }
    }
}
