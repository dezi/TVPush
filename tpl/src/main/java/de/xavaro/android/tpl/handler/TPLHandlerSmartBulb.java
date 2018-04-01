package de.xavaro.android.tpl.handler;

import org.json.JSONObject;

import de.xavaro.android.tpl.base.TPL;

import de.xavaro.android.tpl.simple.Simple;
import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.simple.Log;

public class TPLHandlerSmartBulb extends TPLHandler
{
    private static final String LOGTAG = TPLHandlerSmartBulb.class.getSimpleName();

    public static final String STATE_LIGHT_MODE_NORMAL = "normal";
    public static final String STATE_LIGHT_MODE_CIRCADIAN = "circadian";

    public static void sendAllBulbsOnOff(boolean on)
    {
        sendBulbOnOff(null, on);
    }

    public static void sendBulbOnOff(String ipaddr, boolean on)
    {
        JSONObject message = new JSONObject();
        JSONObject service = new JSONObject();
        JSONObject transition = new JSONObject();

        Json.put(message, "smartlife.iot.smartbulb.lightingservice", service);
        Json.put(service, "transition_light_state", transition);
        Json.put(transition, "ignore_default", 1);
        Json.put(transition, "on_off", on ? 1 : 0);

        if ((ipaddr != null) && ! ipaddr.isEmpty())
        {
            JSONObject destination = new JSONObject();
            Json.put(destination, "ipaddr", ipaddr);

            Json.put(message, "destination", destination);
        }

        TPL.instance.message.sendMessage(message);
    }

    public static void sendAllBulbsHSB(int hue, int saturation, int brightness)
    {
        sendBulbHSB(null, hue, saturation, brightness);
    }

    public static void sendBulbHSB(String ipaddr, int hue, int saturation, int brightness)
    {
        if (hue < 0) hue = 0;
        if (hue > 360) hue = 360;

        if (saturation < 0) saturation = 0;
        if (saturation > 100) saturation = 100;

        if (brightness < 0) brightness = 0;
        if (brightness > 100) brightness = 100;

        JSONObject message = new JSONObject();
        JSONObject service = new JSONObject();
        JSONObject transition = new JSONObject();

        Json.put(message, "smartlife.iot.smartbulb.lightingservice", service);
        Json.put(service, "transition_light_state", transition);
        Json.put(transition, "ignore_default", 1);
        Json.put(transition, "transition_period", 1000);
        Json.put(transition, "mode", STATE_LIGHT_MODE_NORMAL);
        Json.put(transition, "hue", hue);
        Json.put(transition, "saturation", saturation);
        Json.put(transition, "brightness", brightness);
        Json.put(transition, "color_temp", 0);

        if ((ipaddr != null) && ! ipaddr.isEmpty())
        {
            JSONObject destination = new JSONObject();
            Json.put(destination, "ipaddr", ipaddr);

            Json.put(message, "destination", destination);
        }

        TPL.instance.message.sendMessage(message);
    }

    public static void sendBulbHSOnly(String ipaddr, int hue, int saturation)
    {
        if (hue < 0) hue = 0;
        if (hue > 360) hue = 360;

        if (saturation < 0) saturation = 0;
        if (saturation > 100) saturation = 100;

        JSONObject message = new JSONObject();
        JSONObject service = new JSONObject();
        JSONObject transition = new JSONObject();

        Json.put(message, "smartlife.iot.smartbulb.lightingservice", service);
        Json.put(service, "transition_light_state", transition);
        Json.put(transition, "ignore_default", 1);
        Json.put(transition, "transition_period", 1000);
        Json.put(transition, "mode", STATE_LIGHT_MODE_NORMAL);
        Json.put(transition, "hue", hue);
        Json.put(transition, "saturation", saturation);
        Json.put(transition, "color_temp", 0);

        if ((ipaddr != null) && ! ipaddr.isEmpty())
        {
            JSONObject destination = new JSONObject();
            Json.put(destination, "ipaddr", ipaddr);

            Json.put(message, "destination", destination);
        }

        TPL.instance.message.sendMessage(message);
    }

    public static void sendAllBulbsBrightness(int brightness)
    {
        sendBulbBrightness(null, brightness);
    }

    public static void sendBulbBrightness(String ipaddr, int brightness)
    {
        if (brightness < 0) brightness = 0;
        if (brightness > 100) brightness = 100;

        JSONObject message = new JSONObject();
        JSONObject service = new JSONObject();
        JSONObject transition = new JSONObject();

        Json.put(message, "smartlife.iot.smartbulb.lightingservice", service);
        Json.put(service, "transition_light_state", transition);
        Json.put(transition, "ignore_default", 1);
        Json.put(transition, "transition_period", 1000);
        Json.put(transition, "mode", STATE_LIGHT_MODE_NORMAL);
        Json.put(transition, "brightness", brightness);
        Json.put(transition, "color_temp", 0);

        if ((ipaddr != null) && ! ipaddr.isEmpty())
        {
            JSONObject destination = new JSONObject();
            Json.put(destination, "ipaddr", ipaddr);

            Json.put(message, "destination", destination);
        }

        TPL.instance.message.sendMessage(message);
    }

    @Override
    public void onMessageReived(JSONObject message)
    {
        Log.d(LOGTAG, Json.toPretty(message));

        JSONObject service = Json.getObject(message, "smartlife.iot.smartbulb.lightingservice");
        JSONObject light_state = Json.getObject(service, "transition_light_state");
        JSONObject origin = Json.getObject(message, "origin");

        String ipaddr = Json.getString(origin, "ipaddr");
        int ipport = Json.getInt(origin, "ipport");

        String uuid = TPLHandlerSysInfo.resolveUUID(ipaddr);
        String wifi = Simple.getConnectedWifiName();

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);
        Json.put(status, "wifi", wifi);
        Json.put(status, "ipaddr", ipaddr);
        Json.put(status, "ipport", ipport);

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
}
