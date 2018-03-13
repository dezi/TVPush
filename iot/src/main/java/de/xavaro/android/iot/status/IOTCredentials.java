package de.xavaro.android.iot.status;

import android.util.Log;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTCredentials extends IOTList
{
    private final static String LOGTAG = IOTCredentials.class.getSimpleName();

    public static IOTCredentials instance = new IOTCredentials();

    private IOTCredentials()
    {
        super((new IOTCredential()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTCredential(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTCredential getEntry(String uuid)
    {
        return (IOTCredential) instance.list.get(uuid);
    }

    public static void addEntry(IOTCredential newCredential, boolean external)
    {
        IOTCredential oldStatus = getEntry(newCredential.uuid);

        if (oldStatus == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newCredential.uuid);

            if (newCredential.saveToStorage())
            {
                instance.list.put(newCredential.uuid, newCredential);
            }
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldStatus.uuid);

            oldStatus.checkAndMergeContent(newCredential, external);
        }
    }
}
