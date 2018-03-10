package zz.top.tpl.handler;

import android.util.Log;

import org.json.JSONObject;

import zz.top.cam.Cameras;
import zz.top.p2p.camera.P2PUtil;
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

        TPL.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        //Log.d(LOGTAG, Json.toPretty(message));

        JSONObject system = Json.getObject(message, "system");

        buildDeviceDescription(system);
    }

    private void buildDeviceDescription(JSONObject system)
    {
        JSONObject sysinfo = Json.getObject(system, "get_sysinfo");

        if (! Json.has(sysinfo, "deviceId"))
        {
            //
            // Empty reply feedback from broadcast. Ignore.
            //

            return;
        }

        JSONObject origin = Json.getObject(system, "origin");

        String brand = "TP-LINK";

        String id = Json.getString(sysinfo, "deviceId");
        String name = Json.getString(sysinfo, "alias");
        String nick = Json.getString(sysinfo, "alias");
        String model = Json.getString(sysinfo, "model");

        String version = Json.getString(sysinfo, "hw_ver")
                + " - " + Json.getString(sysinfo, "sw_ver");

        String ssid = Simple.getConnectedWifiName();
        String mac = Json.getString(sysinfo, "mac");
        String ip = Json.getString(origin, "ipaddr");
        int port = Json.getInt(origin, "ipport");

        String type = "smartplug";
        String driver = "tpl";

        String capabilities = getCapabilities(Json.getString(sysinfo, "type"));

        String uuid = P2PUtil.hmacSha1UUID(id, mac);

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

        Json.put(network, "ip", ip);
        Json.put(network, "port", port);
        Json.put(network, "ssid", ssid);
        Json.put(network, "mac", mac);

        TPL.cloud.onDeviceFound(tplinkdev);
    }

    private static String getCapabilities(String type)
    {
        // @formatter:off

        String caps = "smartplug|fixed|tcp|wifi|stupid";

        if ( "IOT.SMARTPLUGSWITCH".equals(type)) return caps + "|energy|timer|plugonoff|ledonoff";

        // @formatter:on

        return caps;
    }
}
