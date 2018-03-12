package zz.top.tpl.handler;

import android.util.Log;

import org.json.JSONObject;

import zz.top.tpl.base.TPL;
import zz.top.utl.Json;
import zz.top.utl.Simple;

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
        Json.put(transition, "transition_period", 100);
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

    @Override
    public void onMessageReived(JSONObject message)
    {
        //Log.d(LOGTAG, message.toString());

        JSONObject origin = Json.getObject(message, "origin");
        Json.put(origin, "ssid", Simple.getConnectedWifiName());

        JSONObject alive = new JSONObject();

        Json.put(alive, "network", origin);

        TPL.instance.onDeviceAlive(alive);
    }
}
