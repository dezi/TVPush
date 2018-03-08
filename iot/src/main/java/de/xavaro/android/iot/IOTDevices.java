package de.xavaro.android.iot;

public class IOTDevices extends IOTList
{
    public static IOTDevices devices = new IOTDevices();

    public IOTDevices()
    {
        super((new IOTDevice()).getClassKey());
    }

    @Override
    public IOTBase loadFromJson(String json)
    {
        return new IOTDevice(json, true);
    }

    public IOTDevice getDevice(String uuid)
    {
        return (IOTDevice) list.get(uuid);
    }
}
