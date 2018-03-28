package de.xavaro.android.iot.status;

import android.util.Log;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTMetadatas extends IOTList
{
    private final static String LOGTAG = IOTMetadatas.class.getSimpleName();

    public static IOTMetadatas instance = new IOTMetadatas();

    private IOTMetadatas()
    {
        super((new IOTMetadata()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTMetadata(json, true);
    }

    public static int getCount()
    {
        return instance.getListSize();
    }

    public static IOTMetadata getEntry(String uuid)
    {
        return (IOTMetadata) instance.getEntryInternal(uuid);
    }

    public static void addEntry(IOTMetadata newMetadata, boolean external)
    {
        IOTMetadata oldStatus = getEntry(newMetadata.uuid);

        if (oldStatus == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newMetadata.uuid);

            if (newMetadata.saveToStorage())
            {
                instance.putEntry(newMetadata);
            }
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldStatus.uuid);

            if (oldStatus.checkAndMergeContent(newMetadata, external) > 0)
            {
                instance.putEntry(oldStatus);
            }
        }
    }
}
