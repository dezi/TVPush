package de.xavaro.android.iot.status;

import android.util.Log;

import org.json.JSONObject;

import java.security.acl.LastOwnerException;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.simple.Json;

public class IOTStatus extends IOTObject
{
    private final static String LOGTAG = IOTStatus.class.getSimpleName();

    public String wifi;
    public String ipaddr;
    public Integer ipport;

    public Integer ledstate;
    public Integer plugstate;
    public Integer bulbstate;

    public Integer hue;
    public Integer saturation;
    public Integer brightness;
    public Integer color_temp;

    public Long lastseen;

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

    public void checkAndMergeContent(IOTStatus check, boolean external)
    {
        // @formatter:off

        if (check.wifi       != null) wifi       = check.wifi;
        if (check.ipaddr     != null) ipaddr     = check.ipaddr;
        if (check.ipport     != null) ipport     = check.ipport;

        if (check.ledstate   != null) ledstate   = check.ledstate;
        if (check.plugstate  != null) plugstate  = check.plugstate;
        if (check.bulbstate  != null) bulbstate  = check.bulbstate;

        if (check.hue        != null) hue        = check.hue;
        if (check.saturation != null) saturation = check.saturation;
        if (check.brightness != null) brightness = check.brightness;
        if (check.color_temp != null) color_temp = check.color_temp;

        // @formatter:on

        lastseen = System.currentTimeMillis();

        //Log.d(LOGTAG, "checkAndMergeContent: json=" + this.toJsonString());

        saveToStorage();
    }
}
