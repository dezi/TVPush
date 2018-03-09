package de.xavaro.android.iot.things;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTDomains extends IOTList
{
    private static IOTDomains instance = new IOTDomains();

    private IOTDomains()
    {
        super((new IOTDomain()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTDomain(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTDomain getEntry(String uuid)
    {
        return (IOTDomain) instance.list.get(uuid);
    }
}
