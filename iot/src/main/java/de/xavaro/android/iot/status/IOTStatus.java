package de.xavaro.android.iot.status;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.simple.Json;

public class IOTStatus extends IOTObject
{
    public String wifi;
    public String ipaddr;
    public int ipport;

    public long lastseen;

    public IOTStatus()
    {
        super();
    }

    public IOTStatus(String uuid)
    {
        super(uuid);
    }

    public IOTStatus(JSONObject json)
    {
        super(json);
    }

    public IOTStatus(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    public static void checkAndMergeContent(JSONObject check, boolean external)
    {
        if (check == null) return;

        String uuid = Json.getString(check, "uuid");
        if (uuid == null) return;

        IOTStatus oldStatus = new IOTStatus(uuid);
        IOTStatus newStatus = new IOTStatus(check);

        oldStatus.checkAndMergeContent(newStatus, external);
    }

    public void checkAndMergeContent(IOTStatus check, boolean external)
    {
        boolean changed = false;

        if (changed |= IOTSimple.isBetter(check.wifi, wifi)) wifi = check.wifi;
        if (changed |= IOTSimple.isBetter(check.ipaddr, ipaddr)) ipaddr = check.ipaddr;
        if (changed |= IOTSimple.isBetter(check.ipport, ipport)) ipport = check.ipport;

        if (changed)
        {
            lastseen = System.currentTimeMillis();

            saveToStorage();
        }
    }
}
