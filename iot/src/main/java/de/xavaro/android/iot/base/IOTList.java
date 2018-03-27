package de.xavaro.android.iot.base;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Prefs;
import de.xavaro.android.iot.simple.Simple;

public abstract class IOTList
{
    private final static String LOGTAG = IOTList.class.getSimpleName();

    public Map<String, IOTObject> list = new HashMap<>();

    public IOTList(String classKey)
    {
        load(classKey);
    }

    public abstract IOTObject loadFromJson(String json);

    private void load(String classKey)
    {
        JSONArray keys = Prefs.searchPreferences(classKey);

        for (int inx = 0; inx < keys.length(); inx++)
        {
            String prefkey = Json.getString(keys, inx);

            String json = Prefs.getString(prefkey);

            IOTObject iotbase = loadFromJson(json);
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

        for (Map.Entry<String, IOTObject> entry : list.entrySet())
        {
            Json.put(result, entry.getKey());
        }

        return result;
    }

    public void putEntry(IOTObject object)
    {
        list.put(object.uuid, object);
    }

    //region Subscriptions implementation.

    private final Map<String, ArrayList<Runnable>> subscriber = new HashMap<>();

    public void subscribe(String uuid, Runnable runnable)
    {
        if (uuid != null)
        {
            synchronized (subscriber)
            {
                ArrayList<Runnable> runners = Simple.getMapRunnables(subscriber, uuid);

                if (runners == null)
                {
                    runners = new ArrayList<>();
                    subscriber.put(uuid, runners);
                }

                if (!runners.contains(runnable))
                {
                    runners.add(runnable);
                }
            }
        }
    }

    public void unsubscribe(String uuid, Runnable runnable)
    {
        if (uuid != null)
        {
            synchronized (subscriber)
            {
                ArrayList<Runnable> runners = Simple.getMapRunnables(subscriber, uuid);

                if (runners != null)
                {
                    if (runners.contains(runnable))
                    {
                        runners.remove(runnable);
                    }

                    if (runners.size() == 0)
                    {
                        subscriber.remove(uuid);
                    }
                }
            }
        }
    }

    public void broadcast(String uuid)
    {
        synchronized (subscriber)
        {
            ArrayList<Runnable> runners = Simple.getMapRunnables(subscriber, uuid);
            if (runners == null) return;

            for (Runnable runner : runners)
            {
                Simple.getHandler().post(runner);
            }
        }
    }

    //endregion Subscriptions implementation.
}
