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

    @Nullable
    public static IOTThing getEntry(String uuid)
    {
        IOTThing thing;

        if ((thing = IOTHuman.list.getEntry(uuid)) != null) return thing;
        if ((thing = IOTDevice.list.getEntry(uuid)) != null) return thing;
        if ((thing = IOTDomain.list.getEntry(uuid)) != null) return thing;
        if ((thing = IOTLocation.list.getEntry(uuid)) != null) return thing;

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

    public static void subscribeThing(String uuid, Runnable runnable)
    {
        IOTThing thing = getEntry(uuid);

        if (thing instanceof IOTHuman) IOTHuman.list.subscribe(uuid, runnable);
        if (thing instanceof IOTDevice) IOTDevice.list.subscribe(uuid, runnable);
        if (thing instanceof IOTDomain) IOTDomain.list.subscribe(uuid, runnable);
        if (thing instanceof IOTLocation) IOTLocation.list.subscribe(uuid, runnable);
    }

    public static void unsubscribeThing(String uuid, Runnable runnable)
    {
        IOTThing thing = getEntry(uuid);

        if (thing instanceof IOTHuman) IOTHuman.list.unsubscribe(uuid, runnable);
        if (thing instanceof IOTDevice) IOTDevice.list.unsubscribe(uuid, runnable);
        if (thing instanceof IOTDomain) IOTDomain.list.unsubscribe(uuid, runnable);
        if (thing instanceof IOTLocation) IOTLocation.list.unsubscribe(uuid, runnable);
    }
}
