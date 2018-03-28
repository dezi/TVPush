package de.xavaro.android.iot.things;

import android.support.annotation.Nullable;

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
}
