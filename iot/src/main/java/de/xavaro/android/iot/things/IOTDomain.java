package de.xavaro.android.iot.things;

import org.json.JSONObject;

@SuppressWarnings("WeakerAccess")
public class IOTDomain extends IOTThing
{
    public String fixedwifi;

    public Double fixedLatFine;
    public Double fixedLonFine;
    public Float fixedAltFine;

    public IOTDomain()
    {
        super();
    }

    public IOTDomain(String uuid)
    {
        super(uuid);
    }

    public IOTDomain(JSONObject json)
    {
        super(json);
    }

    public IOTDomain(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    public int checkAndMergeContent(IOTDomain check, boolean external)
    {
        // @formatter:off

        changedSys = false;
        changedUsr = false;

        changed = false;

        if (nequals(fixedwifi, check.fixedwifi)) fixedwifi = check.fixedwifi;

        changedSys = changed;

        // @formatter:on

        //
        // None.
        //

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
