package de.xavaro.android.iot.things;

import org.json.JSONObject;

@SuppressWarnings("WeakerAccess")
public class IOTDomain extends IOTBase
{
    public String nick;
    public String name;

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

    public void checkAndMergeContent(IOTDomain check, boolean external)
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
            this.name = check.name;
        }

        saveToStorage();
    }
}
