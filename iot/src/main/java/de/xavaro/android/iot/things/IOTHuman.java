package de.xavaro.android.iot.things;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;

@SuppressWarnings("WeakerAccess")
public class IOTHuman extends IOTThing
{
    public static IOTList<IOTHuman> list;

    public String firstname;
    public String middlename;
    public String lastname;

    public IOTHuman()
    {
        super();
    }

    public IOTHuman(String uuid)
    {
        super(uuid);
    }

    public IOTHuman(JSONObject json)
    {
        super(json);
    }

    public static IOTHuman buildLocalHuman()
    {
        IOTHuman local = new IOTHuman();

        local.nick = Simple.getDeviceUserName();

        return local;
    }

    @Override
    public int checkAndMergeContent(IOTObject iotObject, boolean external, boolean publish)
    {
        IOTHuman check = (IOTHuman) iotObject;

        changedSys = false;
        changedUsr = false;

        if (external)
        {
            // @formatter:off

            changed = false;

            if (nequals(name,      check.name      )) name       = check.name;
            if (nequals(nick,      check.nick      )) nick       = check.nick;

            if (nequals(firstname, check.firstname )) firstname  = check.firstname;
            if (nequals(middlename,check.middlename)) middlename = check.middlename;
            if (nequals(lastname,  check.lastname  )) lastname   = check.lastname;

            changedUsr = changed;

            // @formatter:on
        }

        return saveIfChanged(publish);
    }
}
