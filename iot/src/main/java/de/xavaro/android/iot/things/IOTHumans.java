package de.xavaro.android.iot.things;

public class IOTHumans extends IOTList
{
    private static IOTHumans instance = new IOTHumans();

    private IOTHumans()
    {
        super((new IOTHuman()).getClassKey());
    }

    @Override
    public IOTThing loadFromJson(String json)
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
