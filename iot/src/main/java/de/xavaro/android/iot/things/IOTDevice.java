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

    public final String TYPE_PC = "pc";
    public final String TYPE_TV = "tv";
    public final String TYPE_PHONE = "phone";
    public final String TYPE_TABLET = "tablet";
    public final String TYPE_LAPTOP = "laptop";
    public final String TYPE_CAMERA= "camera";

    public String did;
    public String nick;
    public String name;
    public String type;
    public String model;
    public String brand;
    public String driver;
    public String version;
    public String location;
    public String fixedwifi;

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

    public void checkAndMergeContent(IOTDevice check, boolean external)
    {
        boolean changed = false;

        //
        // Update possibly from software update.
        //

        // @formatter:off

        if (changed |= IOTSimple.nequals(did,          check.did         )) did          = check.did;
        if (changed |= IOTSimple.nequals(type,         check.type        )) type         = check.type;
        if (changed |= IOTSimple.nequals(brand,        check.brand       )) brand        = check.brand;
        if (changed |= IOTSimple.nequals(model,        check.model       )) model        = check.model;
        if (changed |= IOTSimple.nequals(driver,       check.driver      )) driver       = check.driver;
        if (changed |= IOTSimple.nequals(version,      check.version     )) version      = check.version;
        if (changed |= IOTSimple.nequals(fixedwifi,    check.fixedwifi   )) fixedwifi    = check.fixedwifi;
        if (changed |= IOTSimple.nequals(capabilities, check.capabilities)) capabilities = check.capabilities;

        // @formatter:on

        if (external)
        {
            //
            // Update possibly from user.
            //

            // @formatter:off

            if (changed |= IOTSimple.nequals(nick,     check.nick    )) nick     = check.nick;
            if (changed |= IOTSimple.nequals(name,     check.name    )) name     = check.name;
            if (changed |= IOTSimple.nequals(location, check.location)) location = check.location;

            // @formatter:on
        }

        if (changed || (time == null) || (time == 0))
        {
            time = System.currentTimeMillis();

            saveToStorage();
        }
    }
}
