package zz.top.tpl.handler;

import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.base.TPL;

import zz.top.utl.Json;
import zz.top.utl.Simple;

public class TPLHandlerSysInfo extends TPLHandler
{
    private static final String LOGTAG = TPLHandlerSysInfo.class.getSimpleName();

    public static void sendSysInfoBroadcast()
    {
        String mess = "{\"system\":{\"get_sysinfo\":{}}}";

        JSONObject message = Json.fromStringObject(mess);

        TPL.instance.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        //Log.d(LOGTAG, Json.toPretty(message));

        buildDeviceDescription(message);
    }

    private void buildDeviceDescription(JSONObject message)
    {
        JSONObject system = Json.getObject(message, "system");
        JSONObject sysinfo = Json.getObject(system, "get_sysinfo");

        if (! Json.has(sysinfo, "deviceId"))
        {
            //
            // Empty reply feedback from broadcast. Ignore.
            //

            return;
        }

        JSONObject origin = Json.getObject(message, "origin");

        String brand = "TP-LINK";

        String id = Json.getString(sysinfo, "deviceId");
        String name = Json.getString(sysinfo, "alias");
        String nick = Json.getString(sysinfo, "alias");
        String model = Json.getString(sysinfo, "model");

        String version = Json.getString(sysinfo, "hw_ver")
                + " - " + Json.getString(sysinfo, "sw_ver");

        String mac = Json.getString(sysinfo, "mac");

        if (mac == null)
        {
            String micmac = Json.getString(sysinfo, "mic_mac");

            if ((micmac != null) && (micmac.length() == 12))
            {
                mac = micmac.substring(0, 2)
                        + ":" + micmac.substring(2, 4)
                        + ":" + micmac.substring(4, 6)
                        + ":" + micmac.substring(6, 8)
                        + ":" + micmac.substring(8, 10)
                        + ":" + micmac.substring(10, 12)
                        ;
            }
        }

        String tpltype = Json.getString(sysinfo, "type");
        if (tpltype == null) tpltype = Json.getString(sysinfo, "mic_type");

        String ssid = Simple.getConnectedWifiName();
        String ipaddr = Json.getString(origin, "ipaddr");
        int ipport = Json.getInt(origin, "ipport");

        String driver = "tpl";

        String type = getDeviceType(tpltype);
        String capabilities = getCapabilities(tpltype, model);

        String uuid = Simple.hmacSha1UUID(id, mac);

        Log.d(LOGTAG, "buildDeviceDescription:"
                + " uuid=" + uuid
                + " model=" + model
                + " name=" + name);

        JSONObject tplinkdev = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(tplinkdev, "device", device);

        JSONObject credentials = new JSONObject();
        Json.put(tplinkdev, "credentials", credentials);

        JSONObject network = new JSONObject();
        Json.put(tplinkdev, "network", network);

        Json.put(device, "uuid", uuid);

        Json.put(device, "did", id);
        Json.put(device, "type", type);
        Json.put(device, "name", name);
        Json.put(device, "nick", nick);
        Json.put(device, "model", model);
        Json.put(device, "brand", brand);
        Json.put(device, "version", version);
        Json.put(device, "capabilities", capabilities);
        Json.put(device, "driver", driver);
        Json.put(device, "location", ssid);
        Json.put(device, "fixedwifi", ssid);

        Json.put(network, "ipaddr", ipaddr);
        Json.put(network, "ipport", ipport);
        Json.put(network, "ssid", ssid);
        Json.put(network, "mac", mac);

        TPL.instance.onDeviceFound(tplinkdev);

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);
        Json.put(status, "wifi", ssid);
        Json.put(status, "ipaddr", ipaddr);
        Json.put(status, "ipport", ipport);

        if (Json.has(sysinfo, "led_off"))
        {
            Json.put(status, "ledstate", (Json.getInt(sysinfo, "led_off") == 1) ? 0 : 1);
        }

        if (Json.has(sysinfo, "relay_state"))
        {
            Json.put(status, "plugstate", Json.getInt(sysinfo, "relay_state"));
        }

        JSONObject light_state = Json.getObject(sysinfo, "light_state");

        if (light_state != null)
        {
            if (Json.has(light_state, "on_off"))
            {
                Json.put(status, "bulbstate", Json.getInt(light_state, "on_off"));
            }

            if (Json.has(light_state, "hue"))
            {
                Json.put(status, "hue", Json.getInt(light_state, "hue"));
            }

            if (Json.has(light_state, "saturation"))
            {
                Json.put(status, "saturation", Json.getInt(light_state, "saturation"));
            }

            if (Json.has(light_state, "brightness"))
            {
                Json.put(status, "brightness", Json.getInt(light_state, "brightness"));
            }

            if (Json.has(light_state, "color_temp"))
            {
                Json.put(status, "color_temp", Json.getInt(light_state, "color_temp"));
            }
        }

        TPL.instance.onDeviceStatus(status);
    }

    private static String getDeviceType(String type)
    {
        if ( "IOT.SMARTBULB".equals(type)) return "smartbuld";

        if ( "IOT.SMARTPLUGSWITCH".equals(type)) return "smartplug";

        return "unknown";
    }

    private static String getCapabilities(String type, String model)
    {
        if ( "IOT.SMARTBULB".equals(type))
        {
            String caps = "smartbulb|fixed|tcp|wifi|stupid|bulbonoff";

            if (model.equals("LB120(EU)"))
            {
                caps += "|dimmable|color|colortemp";
            }

            if (model.equals("LB130(EU)"))
            {
                caps += "|dimmable|color|colorhsb|colortemp";
            }

            return caps;
        }

        if ( "IOT.SMARTPLUGSWITCH".equals(type))
        {
            return "smartplug|fixed|tcp|wifi|stupid|energy|timer|plugonoff|ledonoff";
        }

        return "unknown";
    }
}
