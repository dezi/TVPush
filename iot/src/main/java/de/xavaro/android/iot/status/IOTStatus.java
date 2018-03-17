package de.xavaro.android.iot.status;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;

public class IOTStatus extends IOTObject
{
    private final static String LOGTAG = IOTStatus.class.getSimpleName();

    //
    // These are real classes not primitives because
    // they can be missing in JSON if null.
    // Do not fuck with this.
    //

    public String wifi;
    public String ipaddr;
    public Integer ipport;

    public Integer camblind;

    public Integer ledstate;
    public Integer plugstate;
    public Integer bulbstate;

    public Integer hue;
    public Integer saturation;
    public Integer brightness;
    public Integer color_temp;

    public Double gpsLat;
    public Double gpsLon;

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
        changed = false;

        // @formatter:off

        if (nequals(wifi,       check.wifi      )) wifi       = check.wifi;
        if (nequals(ipaddr,     check.ipaddr    )) ipaddr     = check.ipaddr;
        if (nequals(ipport,     check.ipport    )) ipport     = check.ipport;

        if (nequals(camblind,   check.camblind  )) camblind   = check.camblind;

        if (nequals(ledstate,   check.ledstate  )) ledstate   = check.ledstate;
        if (nequals(plugstate,  check.plugstate )) plugstate  = check.plugstate;
        if (nequals(bulbstate,  check.bulbstate )) bulbstate  = check.bulbstate;

        if (nequals(hue,        check.hue       )) hue        = check.hue;
        if (nequals(saturation, check.saturation)) saturation = check.saturation;
        if (nequals(brightness, check.brightness)) brightness = check.brightness;
        if (nequals(color_temp, check.color_temp)) color_temp = check.color_temp;

        if (nequals(gpsLat,     check.gpsLat    )) gpsLat     = check.gpsLat;
        if (nequals(gpsLon,     check.gpsLon    )) gpsLon     = check.gpsLon;

        // @formatter:on

        if (changed || (time == null) || (time == 0))
        {
            time = System.currentTimeMillis();

            saveToStorage();
        }
    }
}
