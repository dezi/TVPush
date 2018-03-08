package de.xavaro.android.iot;

public class IOTDomains extends IOTList
{
    public static IOTDomains domains = new IOTDomains();

    public IOTDomains()
    {
        super((new IOTDomain()).getClassKey());
    }

    @Override
    public IOTBase loadFromJson(String json)
    {
        return new IOTDomain(json, true);
    }

    public IOTDomain getHuman(String uuid)
    {
        return (IOTDomain) list.get(uuid);
    }
}
