package de.xavaro.android.iot.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.simple.Log;
import de.xavaro.android.iot.simple.Simple;

public class IOTStatusses extends IOTList
{
    private final static String LOGTAG = IOTStatusses.class.getSimpleName();

    public static IOTStatusses instance = new IOTStatusses();

    private IOTStatusses()
    {
        super((new IOTStatus()).getClassKey());
    }

    @Override
    public IOTObject loadFromJson(String json)
    {
        return new IOTStatus(json, true);
    }

    public static IOTStatus getEntry(String uuid)
    {
        return (IOTStatus) instance.list.get(uuid);
    }

    public static int addEntry(IOTStatus newStatus, boolean external)
    {
        int result;

        IOTStatus oldStatus = getEntry(newStatus.uuid);

        if (oldStatus == null)
        {
            Log.d(LOGTAG, "addEntry: new uuid=" + newStatus.uuid);

            result = newStatus.saveToStorage()
                    ? IOTDefs.IOT_SAVE_ALLCHANGED
                    : IOTDefs.IOT_SAVE_FAILED;

            if (result > 0) instance.putEntry(newStatus);
        }
        else
        {
            Log.d(LOGTAG, "addEntry: old uuid=" + oldStatus.uuid);

            result = oldStatus.checkAndMergeContent(newStatus, external);

            if (result > 0) instance.putEntry(oldStatus);
        }

        if (result > 0) IOTStatusses.instance.broadcast(newStatus.uuid);

        return result;
    }
}
