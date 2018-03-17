package de.xavaro.android.iot.things;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTLocations extends IOTList
{
    private static IOTLocations instance = new IOTLocations();

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
}
