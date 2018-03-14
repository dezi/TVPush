package de.xavaro.android.iot.status;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.simple.Json;

public class IOTMetadata extends IOTObject
{
    private final static String LOGTAG = IOTMetadata.class.getSimpleName();

    public JSONObject metadata;

    public Long lastseen;

    public IOTMetadata()
    {
        super();
    }

    public IOTMetadata(String uuid)
    {
        super(uuid);
    }

    public IOTMetadata(JSONObject json)
    {
        super(json);
    }

    public IOTMetadata(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    public void checkAndMergeContent(IOTMetadata check, boolean external)
    {
        metadata = check.metadata;

        lastseen = System.currentTimeMillis();

        //Log.d(LOGTAG, "checkAndMergeContent: json=" + this.toJsonString());

        saveToStorage();
    }
}
