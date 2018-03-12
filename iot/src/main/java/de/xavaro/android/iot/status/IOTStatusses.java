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

    public static void addEntry(IOTStatus newDevice, boolean external)
    {
        IOTStatus oldDevice = getEntry(newDevice.uuid);

        if (oldDevice == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newDevice.uuid);

            if (newDevice.saveToStorage())
            {
                instance.list.put(newDevice.uuid, newDevice);
            }
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldDevice.uuid);

            oldDevice.checkAndMergeContent(newDevice, external);
        }
    }
}
