package de.xavaro.android.iot.things;

public class IOTDomains extends IOTList
{
    private static IOTDomains instance = new IOTDomains();

    private IOTDomains()
    {
        super((new IOTDomain()).getClassKey());
    }

    @Override
    public IOTThing loadFromJson(String json)
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
