package de.xavaro.android.iot.things;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.base.IOTSimple;
import de.xavaro.android.iot.simple.Json;
import de.xavaro.android.iot.simple.Simple;

@SuppressWarnings("WeakerAccess")
public class IOTDevice extends IOTObject
{
    private final static String LOGTAG = IOTDevice.class.getSimpleName();

    public final static String TYPE_PC = "pc";
    public final static String TYPE_TV = "tv";
    public final static String TYPE_PHONE = "phone";
    public final static String TYPE_TABLET = "tablet";
    public final static String TYPE_LAPTOP = "laptop";
    public final static String TYPE_CAMERA = "camera";
    public final static String TYPE_TVREMOTE = "tvremote";
    public final static String TYPE_SMARTBULB = "bulb";
    public final static String TYPE_SMARTPLUG = "plug";
    public final static String TYPE_BEACON = "beacon";

    public String did;
    public String nick;
    public String name;
    public String type;
    public String model;
    public String brand;
    public String driver;
    public String version;
    public String location;

    public String macaddr;
    public String fixedwifi;

    public Double fixedLatFine;
    public Double fixedLonFine;
    public Float fixedAltFine;

    public Double fixedLatCoarse;
    public Double fixedLonCoarse;
    public Float fixedAltCoarse;

    public JSONArray capabilities;

    public IOTDevice()
    {
        super();
    }

    public IOTDevice(String uuid)
    {
        super(uuid);
    }

    public IOTDevice(JSONObject json)
    {
        super(json);
    }

    public IOTDevice(String jsonstr, boolean dummy)
    {
        super(jsonstr, dummy);
    }

    public boolean hasCapability(String capability)
    {
        if (capabilities != null)
        {
            for (int inx = 0; inx < capabilities.length(); inx++)
            {
                String devcap = Json.getString(capabilities, inx);
                if (devcap == null) continue;

                if (devcap.equals(capability)) return true;
            }
        }

        return false;
    }

    public static IOTDevice buildLocalDevice()
    {
        IOTDevice local = new IOTDevice();

        local.did = Simple.getDeviceId();
        local.type = Simple.getDeviceType();
        local.nick = Simple.getDeviceUserName();
        local.name = Simple.getDeviceUserName();
        local.brand = Simple.getDeviceBrandName();
        local.model = Simple.getDeviceModelName();
        local.version =  Simple.getAndroidVersion();
        local.location = Simple.getConnectedWifiName();

        local.driver = "iot";

        local.capabilities = Json.jsonArrayFromSeparatedString(Simple.getDeviceCapabilities(), "\\|");

        return local;
    }

    public static void checkAndMergeContent(JSONObject check, boolean external)
    {
        if (check == null) return;

        String deviceUUID = Json.getString(check, "uuid");
        if (deviceUUID == null) return;

        IOTDevice oldDevice = IOTDevices.getEntry(deviceUUID);

        if (oldDevice == null)
        {
            oldDevice = new IOTDevice(check);
            oldDevice.saveToStorage();
        }
        else
        {
            IOTDevice newdevice = new IOTDevice(check);
            oldDevice.checkAndMergeContent(newdevice, external);
        }
    }

    public int checkAndMergeContent(IOTDevice check, boolean external)
    {
        // @formatter:off

        changed = false;

        if (nequals(did,          check.did         )) did          = check.did;
        if (nequals(type,         check.type        )) type         = check.type;
        if (nequals(brand,        check.brand       )) brand        = check.brand;
        if (nequals(model,        check.model       )) model        = check.model;
        if (nequals(driver,       check.driver      )) driver       = check.driver;
        if (nequals(version,      check.version     )) version      = check.version;
        if (nequals(macaddr,      check.macaddr     )) macaddr      = check.macaddr;
        if (nequals(fixedwifi,    check.fixedwifi   )) fixedwifi    = check.fixedwifi;
        if (nequals(capabilities, check.capabilities)) capabilities = check.capabilities;

        if (nequals(fixedLatCoarse, check.fixedLatCoarse)) fixedLatCoarse = check.fixedLatCoarse;
        if (nequals(fixedLonCoarse, check.fixedLonCoarse)) fixedLonCoarse = check.fixedLonCoarse;
        if (nequals(fixedAltCoarse, check.fixedAltCoarse)) fixedAltCoarse = check.fixedAltCoarse;

        changedSys = changed;

        // @formatter:on

        if (external)
        {
            // @formatter:off

            changed = false;

            if (nequals(nick,     check.nick    )) nick     = check.nick;
            if (nequals(name,     check.name    )) name     = check.name;
            if (nequals(location, check.location)) location = check.location;

            if (nequals(fixedLatFine, check.fixedLatFine)) fixedLatFine = check.fixedLatFine;
            if (nequals(fixedLonFine, check.fixedLonFine)) fixedLonFine = check.fixedLonFine;
            if (nequals(fixedAltFine, check.fixedAltFine)) fixedAltFine = check.fixedAltFine;

            changedUsr = changed;

            // @formatter:on
        }

        return saveIfChanged();
    }
}
