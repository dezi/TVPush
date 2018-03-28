package de.xavaro.android.iot.status;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTDefs;
import de.xavaro.android.iot.base.IOTListGeneric;

import de.xavaro.android.iot.simple.Log;

public class IOTStatussesGeneric extends IOTListGeneric<IOTStatus>
{
    private final static String LOGTAG = IOTStatussesGeneric.class.getSimpleName();

    public static IOTStatussesGeneric instance = new IOTStatussesGeneric();

    private IOTStatussesGeneric()
    {
        super((new IOTStatus()).getClassKey());
    }

    @Override
    public IOTStatus loadFromJson(JSONObject json)
    {
        return new IOTStatus(json);
    }

    public static IOTStatus getEntry(String uuid)
    {
        return instance.getEntryInternal(uuid);
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

            if (result > 0)
            {
                Log.d(LOGTAG, "addEntry: diff=" + oldStatus.getChangedDiff());

                instance.putEntry(oldStatus);
            }
        }

        if (result > 0) IOTStatussesGeneric.instance.broadcast(newStatus.uuid);

        return result;
    }
}
