package de.xavaro.android.iot.things;

import android.util.Log;

import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTDomains extends IOTList
{
    private final static String LOGTAG = IOTDomains.class.getSimpleName();

    public static IOTDomains instance = new IOTDomains();

    private IOTDomains()
    {
        super((new IOTDomain()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTDomain(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTDomain getEntry(String uuid)
    {
        return (IOTDomain) instance.getEntryInternal(uuid);
    }

    public static int addEntry(IOTDomain newDomain, boolean external)
    {
        int result;

        IOTDomain oldDomain = getEntry(newDomain.uuid);

        if (oldDomain == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newDomain.uuid);

            result = newDomain.saveToStorage()
                    ? IOTDefs.IOT_SAVE_ALLCHANGED
                    : IOTDefs.IOT_SAVE_FAILED;

            if (result > 0) instance.putEntry(newDomain);
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldDomain.uuid);

            result = oldDomain.checkAndMergeContent(newDomain, external);

            if (result > 0) instance.putEntry(oldDomain);
        }

        return result;
    }
}
