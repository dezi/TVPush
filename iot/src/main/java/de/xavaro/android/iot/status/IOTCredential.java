package de.xavaro.android.iot.status;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTListGeneric;
import de.xavaro.android.iot.base.IOTObject;

public class IOTCredential extends IOTObject
{
    private final static String LOGTAG = IOTCredential.class.getSimpleName();

    public static IOTListGeneric<IOTCredential> list;

    public JSONObject credentials;

    public IOTCredential()
    {
        super();
    }

    public IOTCredential(String uuid)
    {
        super(uuid);
    }

    public IOTCredential(JSONObject json)
    {
        super(json);
    }

    public IOTCredential(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    public int checkAndMergeContent(IOTCredential check, boolean external)
    {
        changedSys = false;
        changedUsr = false;

        changed = false;

        if (nequals(credentials, check.credentials)) credentials = check.credentials;

        changedSys = changed;

        return saveIfChanged();
    }
}
