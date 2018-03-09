package de.xavaro.android.iot.things;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.simple.Json;
import de.xavaro.android.simple.Simple;

@SuppressWarnings("WeakerAccess")
public class IOTDevice extends IOTObject
{
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

    public JSONArray capabilities = new JSONArray();

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
            oldDevice.checkAndMergeContent(newdevice, true);
        }
    }

    public void checkAndMergeContent(IOTDevice check, boolean external)
    {
        //
        // Update possibly from software update.
        //

        this.did = check.did;
        this.type = check.type;
        this.brand = check.brand;
        this.model = check.model;
        this.driver = check.driver;
        this.version = check.version;
        this.fixedwifi = check.fixedwifi;
        this.capabilities = check.capabilities;

        if (external)
        {
            //
            // Update possibly from user.
            //

            this.nick = check.nick;
            this.name = check.name;
            this.location = check.location;
        }

        saveToStorage();
    }
}
