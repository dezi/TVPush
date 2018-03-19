package de.xavaro.android.iot.status;

import android.util.Log;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTStatusses extends IOTList
{
    private final static String LOGTAG = IOTStatusses.class.getSimpleName();

    public static IOTStatusses instance = new IOTStatusses();

    private IOTStatusses()
    {
        super((new IOTStatus()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTStatus(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTStatus getEntry(String uuid)
    {
        return (IOTStatus) instance.list.get(uuid);
    }

    public static void addEntry(IOTStatus newStatus, boolean external)
    {
        IOTStatus oldStatus = getEntry(newStatus.uuid);

        if (oldStatus == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newStatus.uuid);

            newStatus.saveToStorage();
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldStatus.uuid);

            oldStatus.checkAndMergeContent(newStatus, external);
        }
    }
}
