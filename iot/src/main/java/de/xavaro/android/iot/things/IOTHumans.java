package de.xavaro.android.iot.things;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTHumans extends IOTList
{
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
}
