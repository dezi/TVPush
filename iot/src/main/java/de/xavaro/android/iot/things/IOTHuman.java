package de.xavaro.android.iot.things;

@SuppressWarnings("WeakerAccess")
public class IOTHuman extends IOTBase
{
    public String nick;

    public String firstname;
    public String middlename;
    public String lastname;

    public IOTHuman()
    {
        super();
    }

    public IOTHuman(String uuid)
    {
        super(uuid);
    }

    public IOTHuman(String json, boolean dummy)
    {
        super(json, dummy);
    }
}
