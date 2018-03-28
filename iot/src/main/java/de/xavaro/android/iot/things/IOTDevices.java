package de.xavaro.android.iot.things;

import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.simple.Log;

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
        return (IOTDevice) instance.getEntryInternal(uuid);
    }

    public static int addEntry(IOTDevice newDevice, boolean external)
    {
        int result;

        IOTDevice oldDevice = getEntry(newDevice.uuid);

        if (oldDevice == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newDevice.uuid);

            result = newDevice.saveToStorage()
                    ? IOTDefs.IOT_SAVE_ALLCHANGED
                    : IOTDefs.IOT_SAVE_FAILED;

            if (result > 0) instance.putEntry(newDevice);
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldDevice.uuid);

            result = oldDevice.checkAndMergeContent(newDevice, external);

            if (result > 0)
            {
                Log.d(LOGTAG, "addEntry: diff=" + oldDevice.getChangedDiff());

                instance.putEntry(oldDevice);
            }
        }

        if (result > 0) IOTDevices.instance.broadcast(newDevice.uuid);

        return result;
    }
}
