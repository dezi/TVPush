package de.xavaro.android.iot.things;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;

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

    public IOTThing(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }
}
