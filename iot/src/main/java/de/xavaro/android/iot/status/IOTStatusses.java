package de.xavaro.android.iot.status;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTListGeneric;

public class IOTStatusses extends IOTListGeneric<IOTStatus>
{
    private final static String LOGTAG = IOTStatusses.class.getSimpleName();

    public static IOTStatusses instance = new IOTStatusses();

    private IOTStatusses()
    {
        super((new IOTStatus()).getClassKey());
    }

    @Override
    public IOTStatus loadFromJson(JSONObject json)
    {
        return new IOTStatus(json);
    }

    public static int addEntryx(IOTStatus newStatus, boolean external)
    {
        return instance.addEntryInternal(newStatus, external);
    }
}
