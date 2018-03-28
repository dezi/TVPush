package de.xavaro.android.iot.things;

import android.support.annotation.Nullable;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;

import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTMetadata;
import de.xavaro.android.iot.status.IOTStatus;

public abstract class IOTThing extends IOTObject
{
    public String nick;
    public String name;

    public IOTThing()
    {
        super();
    }

    public IOTThing(String uuid)
    {
        super(uuid);
    }

    public IOTThing(JSONObject json)
    {
        super(json);
    }

    public IOTThing(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    @Nullable
    public static IOTThing getEntry(String uuid)
    {
        IOTThing thing;

        if ((thing = IOTHuman.list.getEntryInternal(uuid)) != null) return thing;
        if ((thing = IOTDevice.list.getEntryInternal(uuid)) != null) return thing;
        if ((thing = IOTDomain.list.getEntryInternal(uuid)) != null) return thing;
        if ((thing = IOTLocation.list.getEntryInternal(uuid)) != null) return thing;

        return null;
    }

    public static void deleteThing(String uuid)
    {
        IOTHuman.list.removeEntry(uuid);
        IOTDevice.list.removeEntry(uuid);
        IOTDomain.list.removeEntry(uuid);
        IOTLocation.list.removeEntry(uuid);

        IOTStatus.list.removeEntry(uuid);
        IOTMetadata.list.removeEntry(uuid);
        IOTCredential.list.removeEntry(uuid);
    }
}
