package de.xavaro.android.iot.things;

import android.util.Log;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTDevices extends IOTList
{
    private final static String LOGTAG = IOTDevices.class.getSimpleName();

    public static IOTDevices instance = new IOTDevices();

    private IOTDevices()
    {
        super((new IOTDevice()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
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

            newDevice.saveToStorage();
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldDevice.uuid);

            oldDevice.checkAndMergeContent(newDevice, external);
        }
    }
}
