package de.xavaro.android.iot.things;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Prefs;

public abstract class IOTBaseList
{
    private final static String LOGTAG = IOTBaseList.class.getSimpleName();

    public Map<String, IOTBaseThing> list = new HashMap<>();

    public IOTBaseList(String classKey)
    {
        load(classKey);
    }

    public abstract IOTBaseThing loadFromJson(String json);

    private void load(String classKey)
    {
        JSONArray keys = Prefs.searchPreferences(classKey);

        for (int inx = 0; inx < keys.length(); inx++)
        {
            String prefkey = Json.getString(keys, inx);

            String json = Prefs.getString(prefkey);

            IOTBaseThing iotbase = loadFromJson(json);
            if (iotbase == null) continue;

            list.put(iotbase.uuid, iotbase);
        }
    }

    public int getListSize()
    {
        return list.size();
    }

    public JSONArray getListUUIDs()
    {
        JSONArray result = new JSONArray();

        for (Map.Entry<String, IOTBaseThing> entry : list.entrySet())
        {
            Json.put(result, entry.getKey());
        }

        return result;
    }
}
