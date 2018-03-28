package de.xavaro.android.iot.status;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

public class IOTMetadata extends IOTObject
{
    private final static String LOGTAG = IOTMetadata.class.getSimpleName();

    public static IOTList<IOTMetadata> list;

    public JSONObject metadata;

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

    @Override
    public int checkAndMergeContent(IOTObject iotCheck, boolean external, boolean publish)
    {
        IOTMetadata check = (IOTMetadata) iotCheck;

        changedSys = false;
        changedUsr = false;

        changed = false;

        if (nequals(metadata, check.metadata)) metadata = check.metadata;

        changedSys = changed;

        return saveIfChanged(publish);
    }
}
