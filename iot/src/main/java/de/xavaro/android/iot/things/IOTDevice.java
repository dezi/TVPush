package de.xavaro.android.iot.things;

import android.os.Build;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTList;
import de.xavaro.android.iot.base.IOTObject;

import de.xavaro.android.iot.simple.Simple;
import de.xavaro.android.iot.simple.Json;

@SuppressWarnings("WeakerAccess")
public class IOTDevice extends IOTThing
{
    private final static String LOGTAG = IOTDevice.class.getSimpleName();

    public static IOTList<IOTDevice> list;

    public final static String TYPE_PC = "pc";
    public final static String TYPE_TV = "tv";
    public final static String TYPE_PHONE = "phone";
    public final static String TYPE_TABLET = "tablet";
    public final static String TYPE_LAPTOP = "laptop";
    public final static String TYPE_CAMERA = "camera";
    public final static String TYPE_BEACON = "beacon";
    public final static String TYPE_TVREMOTE = "tvremote";
    public final static String TYPE_SMARTBULB = "smartbulb";
    public final static String TYPE_SMARTPLUG = "smartplug";

    public String did;
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
    public Double fixedAltFine;

    public Double fixedLatCoarse;
    public Double fixedLonCoarse;
    public Double fixedAltCoarse;

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

        local.driver = "iot";

        local.did = Simple.getDeviceId();
        local.type = Simple.getDeviceType();
        local.nick = Simple.getDeviceUserName();
        local.name = Simple.getDeviceUserName();
        local.brand = Simple.getDeviceBrandName();
        local.model = Simple.getDeviceModelName();
        local.version =  Simple.getAndroidVersion();
        local.location = Simple.getConnectedWifiName();
        local.capabilities = getDeviceCapabilities();

        return local;
    }

    private static JSONArray getDeviceCapabilities()
    {
        String caps = "";

        if (Simple.isTV())
        {
            caps += "tv|fixed|hd";

            if (Simple.getDeviceModelName().contains("BRAVIA 4K"))
            {
                caps += "|1080p|uhd|4k|mic";
            }
            else
            {
                if (Simple.getDeviceWidth() >= 1080)
                {
                    caps += "|1080p";
                }
                else
                {
                    caps += "|720p";
                }
            }
        }
        else
        {
            if (Simple.isTablet())
            {
                caps += "tablet|mic";
            }
            else
            {
                if (Simple.isPhone())
                {
                    caps += "phone|mic";
                }
                else
                {
                    caps += "unknown";
                }
            }
        }

        caps += "|speaker|tcp|wifi";

        if (Simple.isGps()) caps += "|gps";
        if (Simple.isTouch()) caps += "|touch";
        if (Simple.isIscamera()) caps += "|camera";
        if (Simple.isWideScreen()) caps += "|widescreen";

        if (Simple.isSpeechIn() && caps.contains("|mic|")) caps += "|spechin";
        if (caps.contains("|speaker|")) caps += "|spechout";

        if ((Simple.getFCMToken() != null) && ! Simple.getFCMToken().isEmpty())
        {
            caps += "|fcm";
        }

        if (Simple.isDeveloper())
        {
            caps += "|devel";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            {
                caps += "|adb";
            }
        }

        return Json.jsonArrayFromSeparatedString(caps, "\\|");
    }

    @Override
    public int checkAndMergeContent(IOTObject iotObject, boolean external, boolean publish)
    {
        IOTDevice check = (IOTDevice) iotObject;

        // @formatter:off

        changedSys = false;
        changedUsr = false;

        changed = false;

        if (nequals(did,          check.did         )) did          = check.did;
        if (nequals(name,         check.name        )) name         = check.name;
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
            if (nequals(location, check.location)) location = check.location;

            if (nequals(fixedLatFine, check.fixedLatFine)) fixedLatFine = check.fixedLatFine;
            if (nequals(fixedLonFine, check.fixedLonFine)) fixedLonFine = check.fixedLonFine;
            if (nequals(fixedAltFine, check.fixedAltFine)) fixedAltFine = check.fixedAltFine;

            changedUsr = changed;

            // @formatter:on
        }

        return saveIfChanged(publish);
    }
}
