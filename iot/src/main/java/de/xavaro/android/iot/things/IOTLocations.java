package de.xavaro.android.iot.things;

import android.util.Log;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTLocations extends IOTList
{
    private final static String LOGTAG = IOTLocations.class.getSimpleName();

    public static IOTLocations instance = new IOTLocations();

    private IOTLocations()
    {
        super((new IOTLocation()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTLocation(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTLocation getEntry(String uuid)
    {
        return (IOTLocation) instance.list.get(uuid);
    }
    
    public static boolean addEntry(IOTLocation newLocation, boolean external)
    {
        IOTLocation oldLocation = getEntry(newLocation.uuid);

        if (oldLocation == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newLocation.uuid);

            return newLocation.saveToStorage();
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldLocation.uuid);

            return oldLocation.checkAndMergeContent(newLocation, external);
        }
    }

}
