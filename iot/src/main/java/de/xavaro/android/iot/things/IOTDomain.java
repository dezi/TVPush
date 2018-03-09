package de.xavaro.android.iot.things;

@SuppressWarnings("WeakerAccess")
public class IOTDomain extends IOTBase
{
    public String nick;
    public String name;

    public IOTDomain()
    {
        super();
    }

    public IOTDomain(String uuid)
    {
        super(uuid);
    }

    public IOTDomain(String json, boolean dummy)
    {
        super(json, dummy);
    }

}
