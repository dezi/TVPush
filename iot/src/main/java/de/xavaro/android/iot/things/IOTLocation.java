package de.xavaro.android.iot.things;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

@SuppressWarnings("WeakerAccess")
public class IOTLocation extends IOTThing
{
    public static IOTList<IOTLocation> list;

    public String nick;
    public String name;

    public Double fixedLatFine;
    public Double fixedLonFine;
    public Float fixedAltFine;

    public IOTLocation()
    {
        super();
    }

    public IOTLocation(String uuid)
    {
        super(uuid);
    }

    public IOTLocation(JSONObject json)
    {
        super(json);
    }

    @Override
    public int checkAndMergeContent(IOTObject iotObject, boolean external, boolean publish)
    {
        IOTLocation check = (IOTLocation) iotObject;

        changedSys = false;
        changedUsr = false;

        if (external)
        {
            // @formatter:off

            changed = false;

            if (nequals(nick,     check.nick    )) nick     = check.nick;
            if (nequals(name,     check.name    )) name     = check.name;

            if (nequals(fixedLatFine, check.fixedLatFine)) fixedLatFine = check.fixedLatFine;
            if (nequals(fixedLonFine, check.fixedLonFine)) fixedLonFine = check.fixedLonFine;
            if (nequals(fixedAltFine, check.fixedAltFine)) fixedAltFine = check.fixedAltFine;

            changedUsr = changed;

            // @formatter:on
        }

        return saveIfChanged(publish);
    }
}
