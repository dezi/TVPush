package de.xavaro.android.iot.things;

import android.support.annotation.Nullable;

import de.xavaro.android.iot.status.IOTCredentials;
import de.xavaro.android.iot.status.IOTMetadatas;
import de.xavaro.android.iot.status.IOTStatusses;

public class IOTThings
{
    @Nullable
    public static IOTThing getEntry(String uuid)
    {
        IOTThing thing;

        if ((thing = (IOTThing) IOTHumans.instance.list.get(uuid)) != null) return thing;
        if ((thing = (IOTThing) IOTDevices.instance.list.get(uuid)) != null) return thing;
        if ((thing = (IOTThing) IOTDomains.instance.list.get(uuid)) != null) return thing;
        if ((thing = (IOTThing) IOTLocations.instance.list.get(uuid)) != null) return thing;

        return null;
    }

    public static void deleteThing(String uuid)
    {
        IOTHumans.instance.removeEntry(uuid);
        IOTDevices.instance.removeEntry(uuid);
        IOTDomains.instance.removeEntry(uuid);
        IOTLocations.instance.removeEntry(uuid);

        IOTStatusses.instance.removeEntry(uuid);
        IOTMetadatas.instance.removeEntry(uuid);
        IOTCredentials.instance.removeEntry(uuid);
    }
}
