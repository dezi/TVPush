package de.xavaro.android.iot.things;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;

@SuppressWarnings("WeakerAccess")
public class IOTLocation extends IOTObject
{
    public String nick;
    public String name;

    public Double fixedLat;
    public Double fixedLon;

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

    public void checkAndMergeContent(IOTLocation check, boolean external)
    {
        changed = false;

        //
        // Update possibly from software update.
        //

        // @formatter:off

        // None

        // @formatter:on

        //
        // None.
        //

        if (external)
        {
            //
            // Update possibly from user.
            //

            // @formatter:off

            if (nequals(nick,     check.nick    )) nick     = check.nick;
            if (nequals(name,     check.name    )) name     = check.name;
            if (nequals(fixedLat, check.fixedLat)) fixedLat = check.fixedLat;
            if (nequals(fixedLon, check.fixedLon)) fixedLon = check.fixedLon;

            // @formatter:on
        }

        if (changed || (time == null) || (time == 0))
        {
            time = System.currentTimeMillis();

            saveToStorage();
        }
    }
}
