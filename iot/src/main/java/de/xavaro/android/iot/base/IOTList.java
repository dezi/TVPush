package de.xavaro.android.iot.base;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.things.IOTHuman;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTLocation;

import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTMetadata;
import de.xavaro.android.iot.status.IOTCredential;

import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.simple.Prefs;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Log;

public class IOTList<T>
{
    private final static String LOGTAG = IOTList.class.getSimpleName();

    private final String classKey;
    private final Map<String, T> list = new HashMap<>();

    public IOTList(String classKey)
    {
        this.classKey = classKey;

        Log.d(LOGTAG, "IOTListGeneric: classKey=" + classKey);

        loadAllFromStorage();
    }

    public int getCount()
    {
        synchronized (list)
        {
            return list.size();
        }
    }

    public JSONArray getUUIDList()
    {
        JSONArray result = new JSONArray();

        synchronized (list)
        {
            for (Map.Entry<String, T> entry : list.entrySet())
            {
                Json.put(result, entry.getKey());
            }
        }

        return result;
    }

    @Nullable
    public T getEntry(String uuid)
    {
        if (uuid != null)
        {
            synchronized (list)
            {
                return list.get(uuid);
            }
        }

        return null;
    }

    @Nullable
    public JSONObject getEntryJson(String uuid)
    {
        T entry = null;

        if (uuid != null)
        {
            synchronized (list)
            {
                entry = list.get(uuid);
            }
        }

        return ((entry != null) && (entry instanceof IOTObject)) ? ((IOTObject) entry).toJson() : null;
    }

    public void putEntry(T iotObject)
    {
        synchronized (list)
        {
            list.put(((IOTObject) iotObject).uuid, iotObject);
        }
    }

    public int addEntry(T newEntry, boolean external, boolean publish)
    {
        String uuid = ((IOTObject) newEntry).uuid;

        int result;

        T oldEntry = getEntry(uuid);

        if (oldEntry == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + uuid);

            result = ((IOTObject) newEntry).saveToStorage(publish)
                    ? IOTDefs.IOT_SAVE_ALLCHANGED
                    : IOTDefs.IOT_SAVE_FAILED;
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + uuid);

            result = ((IOTObject) oldEntry).checkAndMergeContent((IOTObject) newEntry, external, publish);

            if (result > 0)
            {
                Log.d(LOGTAG, "addEntry: diff=" + ((IOTObject) oldEntry).getChangedDiff());
            }
        }

        if (result > 0) broadcast(uuid);

        return result;
    }

    public void removeEntry(String uuid)
    {
        synchronized (list)
        {
            list.remove(uuid);
        }

        Prefs.removePref(classKey + uuid);
    }

    @SuppressWarnings("unchecked")
    private void loadAllFromStorage()
    {
        JSONArray keys = Prefs.searchPreferences(classKey);

        Log.d(LOGTAG, "loadAllFromStorage: classKey=" + classKey + " count=" + keys.length());

        for (int inx = 0; inx < keys.length(); inx++)
        {
            String prefkey = Json.getString(keys, inx);

            String json = Prefs.getString(prefkey);
            JSONObject jsonobj = Json.fromStringObject(json);
            if (jsonobj == null) continue;

            T iotObject = null;

            if (classKey.equals("iot.IOTHuman.")) iotObject = (T) new IOTHuman(jsonobj);
            if (classKey.equals("iot.IOTDevice.")) iotObject = (T) new IOTDevice(jsonobj);
            if (classKey.equals("iot.IOTDomain.")) iotObject = (T) new IOTDomain(jsonobj);
            if (classKey.equals("iot.IOTLocation.")) iotObject = (T) new IOTLocation(jsonobj);

            if (classKey.equals("iot.IOTStatus.")) iotObject = (T) new IOTStatus(jsonobj);
            if (classKey.equals("iot.IOTMetadata.")) iotObject = (T) new IOTMetadata(jsonobj);
            if (classKey.equals("iot.IOTCredential.")) iotObject = (T) new IOTCredential(jsonobj);

            if (iotObject == null) continue;

            synchronized (list)
            {
                list.put(((IOTObject) iotObject).uuid, iotObject);
            }
        }

        Log.d(LOGTAG, "loadAllFromStorage: classKey=" + classKey + " loaded=" + list.size());
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
