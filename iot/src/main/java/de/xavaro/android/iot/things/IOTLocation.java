package de.xavaro.android.iot.things;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTListGeneric;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.status.IOTStatus;

@SuppressWarnings("WeakerAccess")
public class IOTLocation extends IOTThing
{
    public static IOTListGeneric<IOTLocation> list;

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

    public IOTLocation(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    @Override
    public int checkAndMergeContent(IOTObject iotObject, boolean external)
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

        return saveIfChanged();
    }
}
