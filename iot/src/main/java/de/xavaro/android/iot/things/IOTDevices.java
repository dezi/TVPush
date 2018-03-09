package de.xavaro.android.iot.things;

import android.util.Log;

public class IOTDevices extends IOTBaseList
{
    private final static String LOGTAG = IOTDevices.class.getSimpleName();

    private static IOTDevices instance = new IOTDevices();

    private IOTDevices()
    {
        super((new IOTDevice()).getClassKey());
    }

    @Override
    public IOTBaseThing loadFromJson(String json)
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

    public static void addEntry(IOTDevice newDevice, boolean external)
    {
        IOTDevice oldDevice = getEntry(newDevice.uuid);

        if (oldDevice == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newDevice.uuid);

            if (newDevice.saveToStorage())
            {
                instance.list.put(newDevice.uuid, newDevice);
            }
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldDevice.uuid);

            oldDevice.checkAndMergeContent(newDevice, external);
        }
    }
}
