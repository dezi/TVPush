package de.xavaro.android.iot.things;

import android.util.Log;

import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTHumans extends IOTList
{
    private final static String LOGTAG = IOTHumans.class.getSimpleName();

    public static IOTHumans instance = new IOTHumans();

    private IOTHumans()
    {
        super((new IOTHuman()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTHuman(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTHuman getEntry(String uuid)
    {
        return (IOTHuman) instance.list.get(uuid);
    }

    public static int addEntry(IOTHuman newHuman, boolean external)
    {
        IOTHuman oldHuman = getEntry(newHuman.uuid);

        if (oldHuman == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newHuman.uuid);

            return newHuman.saveToStorage()
                    ? IOTDefs.IOT_SAVE_ALLCHANGED
                    : IOTDefs.IOT_SAVE_FAILED;
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldHuman.uuid);

            return oldHuman.checkAndMergeContent(newHuman, external);
        }
    }
}
