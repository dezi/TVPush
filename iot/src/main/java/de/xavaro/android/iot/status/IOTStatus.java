package de.xavaro.android.iot.status;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTAlive;
import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

@SuppressWarnings("WeakerAccess")
public class IOTStatus extends IOTObject
{
    private final static String LOGTAG = IOTStatus.class.getSimpleName();

    public static IOTList<IOTStatus> list;

    //
    // These are real classes not primitives because
    // they can be missing in JSON if null.
    // Do not fuck with this.
    //

    public String wifi;
    public String ipaddr;
    public Integer ipport;
    public String macaddr;

    public Integer txpower;

    public Integer camblind;

    public Integer ledstate;
    public Integer plugstate;
    public Integer bulbstate;

    public Double temperature;
    public Double humidity;

    public Integer airquality;
    public Integer lightlevel;
    public Integer noiselevel;

    public Integer rgb;
    public Integer hue;
    public Integer saturation;
    public Integer brightness;
    public Integer color_temp;
    public Integer white_temp;
    public Integer bulb_mode;

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

    @Override
    public int checkAndMergeContent(IOTObject iotCheck, boolean external, boolean publish)
    {
        IOTStatus check = (IOTStatus) iotCheck;

        // @formatter:off

        changedSys = false;
        changedUsr = false;

        changed = false;

        if (nequals(wifi,        check.wifi       )) wifi        = check.wifi;
        if (nequals(ipaddr,      check.ipaddr     )) ipaddr      = check.ipaddr;
        if (nequals(ipport,      check.ipport     )) ipport      = check.ipport;

        if (nequals(txpower,     check.txpower    )) txpower     = check.txpower;
        if (nequals(macaddr,     check.macaddr    )) macaddr     = check.macaddr;

        if (nequals(camblind,    check.camblind   )) camblind    = check.camblind;

        if (nequals(ledstate,    check.ledstate   )) ledstate    = check.ledstate;
        if (nequals(plugstate,   check.plugstate  )) plugstate   = check.plugstate;
        if (nequals(bulbstate,   check.bulbstate  )) bulbstate   = check.bulbstate;

        if (nequals(temperature, check.temperature)) temperature = check.temperature;
        if (nequals(humidity,    check.humidity   )) humidity    = check.humidity;

        if (nequals(airquality,  check.airquality )) airquality  = check.airquality;
        if (nequals(lightlevel,  check.lightlevel )) lightlevel  = check.lightlevel;
        if (nequals(noiselevel,  check.noiselevel )) noiselevel  = check.noiselevel;

        if (nequals(hue,         check.hue        )) hue         = check.hue;
        if (nequals(saturation,  check.saturation )) saturation  = check.saturation;
        if (nequals(brightness,  check.brightness )) brightness  = check.brightness;
        if (nequals(color_temp,  check.color_temp )) color_temp  = check.color_temp;

        if (nequals(positionLatCoarse, check.positionLatCoarse)) positionLatCoarse = check.positionLatCoarse;
        if (nequals(positionLonCoarse, check.positionLonCoarse)) positionLonCoarse = check.positionLonCoarse;
        if (nequals(positionAltCoarse, check.positionAltCoarse)) positionAltCoarse = check.positionAltCoarse;

        if (nequals(positionLatFine,   check.positionLatFine  )) positionLatFine   = check.positionLatFine;
        if (nequals(positionLonFine,   check.positionLonFine  )) positionLonFine   = check.positionLonFine;
        if (nequals(positionAltFine,   check.positionAltFine  )) positionAltFine   = check.positionAltFine;

        changedSys = changed;

        // @formatter:on

        if (IOT.instance.alive != null)
        {
            IOT.instance.alive.setAliveStatus(uuid);
        }

        return saveIfChanged(publish);
    }
}
