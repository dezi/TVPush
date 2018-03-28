package de.xavaro.android.iot.status;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTCredential extends IOTObject
{
    private final static String LOGTAG = IOTCredential.class.getSimpleName();

    public static IOTList<IOTCredential> list;

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

    @Override
    public int checkAndMergeContent(IOTObject iotCheck, boolean external)
    {
        IOTCredential check = (IOTCredential) iotCheck;

        changedSys = false;
        changedUsr = false;

        changed = false;

        if (nequals(credentials, check.credentials)) credentials = check.credentials;

        changedSys = changed;

        return saveIfChanged();
    }
}
