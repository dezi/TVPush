package de.xavaro.android.iot.things;

public class IOTDevices extends IOTList
{
    private static IOTDevices instance = new IOTDevices();

    private IOTDevices()
    {
        super((new IOTDevice()).getClassKey());
    }

    @Override
    public IOTBase loadFromJson(String json)
    {
        return new IOTDevice(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTDevice getEntry(String uuid)
    {
        return (IOTDevice) instance.list.get(uuid);
    }
}
