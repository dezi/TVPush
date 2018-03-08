package de.xavaro.android.iot;

public class IOTDomains extends IOTList
{
    private static IOTDomains instance = new IOTDomains();

    private IOTDomains()
    {
        super((new IOTDomain()).getClassKey());
    }

    @Override
    public IOTBase loadFromJson(String json)
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
