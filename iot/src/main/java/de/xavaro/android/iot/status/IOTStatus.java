package de.xavaro.android.iot.status;

import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;

@SuppressWarnings("WeakerAccess")
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

    public Integer rssi;
    public Integer txpower;

    public Integer camblind;

    public Integer ledstate;
    public Integer plugstate;
    public Integer bulbstate;

    public Integer hue;
    public Integer saturation;
    public Integer brightness;
    public Integer color_temp;

    public Double positionLatCoarse;
    public Double positionLonCoarse;
    public Double positionAltCoarse;

    public Double positionLatFine;
    public Double positionLonFine;
    public Double positionAltFine;

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

        changed = false;
        changedUsr = false;

        if (nequals(wifi,       check.wifi      )) wifi       = check.wifi;
        if (nequals(ipaddr,     check.ipaddr    )) ipaddr     = check.ipaddr;
        if (nequals(ipport,     check.ipport    )) ipport     = check.ipport;

        if (nequals(rssi,       check.rssi      )) rssi       = check.rssi;
        if (nequals(txpower,    check.txpower   )) txpower    = check.txpower;

        if (nequals(camblind,   check.camblind  )) camblind   = check.camblind;

        if (nequals(ledstate,   check.ledstate  )) ledstate   = check.ledstate;
        if (nequals(plugstate,  check.plugstate )) plugstate  = check.plugstate;
        if (nequals(bulbstate,  check.bulbstate )) bulbstate  = check.bulbstate;

        if (nequals(hue,        check.hue       )) hue        = check.hue;
        if (nequals(saturation, check.saturation)) saturation = check.saturation;
        if (nequals(brightness, check.brightness)) brightness = check.brightness;
        if (nequals(color_temp, check.color_temp)) color_temp = check.color_temp;

        if (nequals(positionLatCoarse, check.positionLatCoarse)) positionLatCoarse = check.positionLatCoarse;
        if (nequals(positionLonCoarse, check.positionLonCoarse)) positionLonCoarse = check.positionLonCoarse;
        if (nequals(positionAltCoarse, check.positionAltCoarse)) positionAltCoarse = check.positionAltCoarse;

        if (nequals(positionLatFine,   check.positionLatFine  )) positionLatFine   = check.positionLatFine;
        if (nequals(positionLonFine,   check.positionLonFine  )) positionLonFine   = check.positionLonFine;
        if (nequals(positionAltFine,   check.positionAltFine  )) positionAltFine   = check.positionAltFine;

        changedSys = changed;

        // @formatter:on

        saveIfChanged();
    }
}
