package de.xavaro.android.iot.things;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Simple;

@SuppressWarnings("WeakerAccess")
public class IOTHuman extends IOTObject
{
    public String nick;

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

    public IOTHuman(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    public static IOTHuman buildLocalHuman()
    {
        IOTHuman local = new IOTHuman();

        local.nick = Simple.getDeviceUserName();

        return local;
    }

    public static void checkAndMergeContent(JSONObject check, boolean external)
    {
        if (check == null) return;

        String humanUUID = Json.getString(check, "uuid");
        if (humanUUID == null) return;

        IOTHuman oldHuman = IOTHumans.getEntry(humanUUID);

        if (oldHuman == null)
        {
            oldHuman = new IOTHuman(check);
            oldHuman.saveToStorage();
        }
        else
        {
            IOTHuman newhuman = new IOTHuman(check);
            oldHuman.checkAndMergeContent(newhuman, true);
        }
    }

    public void checkAndMergeContent(IOTHuman check, boolean external)
    {
        //
        // Update possibly from software update.
        //

        //
        // None.
        //

        if (external)
        {
            //
            // Update possibly from user.
            //

            this.nick = check.nick;
            this.firstname = check.firstname;
            this.middlename = check.middlename;
            this.lastname = check.lastname;
        }

        saveToStorage();
    }
}