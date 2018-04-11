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

    public static boolean sendBulb(String ipaddr, int onOff, int hue, int saturation, int brightness)
    {
        JSONObject message = new JSONObject();
        JSONObject service = new JSONObject();
        JSONObject transition = new JSONObject();

        Json.put(message, "smartlife.iot.smartbulb.lightingservice", service);
        Json.put(service, "transition_light_state", transition);
        Json.put(transition, "ignore_default", 1);
        Json.put(transition, "transition_period", 1000);
        Json.put(transition, "mode", STATE_LIGHT_MODE_NORMAL);
        Json.put(transition, "color_temp", 0);

        if (onOff >= 0) Json.put(transition, "on_off", onOff);

        if (hue >= 0) Json.put(transition, "hue", hue);
        if (saturation >= 0) Json.put(transition, "saturation", saturation);
        if (brightness >= 0) Json.put(transition, "brightness", brightness);

        String result = sendToSocket(ipaddr, message);

        return ((result != null) && result.contains("\"err_code\":0"));
    }

    public static boolean sendBulbOnOff(String ipaddr, int onOff)
    {
        return sendBulb(ipaddr, onOff, -1, -1, -1);
    }

    public static boolean sendBulbHSB(String ipaddr, int hue, int saturation, int brightness)
    {
        return sendBulb(ipaddr, -1, hue, saturation, brightness);
   }

    public static boolean sendBulbHSOnly(String ipaddr, int hue, int saturation)
    {
        return sendBulb(ipaddr, -1, hue, saturation, -1);
    }

    public static boolean sendBulbBrightness(String ipaddr, int brightness)
    {
        return sendBulb(ipaddr, -1,-1, -1, brightness);
    }

    public void onMessageReived(JSONObject message)
    {
        Log.d(LOGTAG, Json.toPretty(message));

        JSONObject service = Json.getObject(message, "smartlife.iot.smartbulb.lightingservice");
        JSONObject light_state = Json.getObject(service, "transition_light_state");

        JSONObject status = new JSONObject();

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
