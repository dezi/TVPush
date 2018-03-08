package de.xavaro.android.iot;

public class IOTHumans extends IOTList
{
    public static IOTHumans humans = new IOTHumans();

    public IOTHumans()
    {
        super((new IOTHuman()).getClassKey());
    }

    @Override
    public IOTBase loadFromJson(String json)
    {
        return new IOTHuman(json, true);
    }

    public IOTHuman getHuman(String uuid)
    {
        return (IOTHuman) list.get(uuid);
    }
}
