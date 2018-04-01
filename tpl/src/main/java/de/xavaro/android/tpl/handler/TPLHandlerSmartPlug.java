package de.xavaro.android.tpl.handler;

import org.json.JSONObject;

import de.xavaro.android.tpl.base.TPL;

import de.xavaro.android.tpl.simple.Json;

public class TPLHandlerSmartPlug extends TPLHandler
{
    private static final String LOGTAG = TPLHandlerSmartPlug.class.getSimpleName();

    public static void sendAllPlugsOnOff(boolean on)
    {
        sendPlugOnOff(null, on);
    }

    public static void sendPlugOnOff(String ipaddr, boolean on)
    {
        String messOn = "{\"system\":{\"set_relay_state\":{\"state\":1}}}";
        String messOff = "{\"system\":{\"set_relay_state\":{\"state\":0}}}";

        JSONObject message = Json.fromStringObject(on ? messOn : messOff);

        if ((ipaddr != null) && ! ipaddr.isEmpty())
        {
            JSONObject destination = new JSONObject();
            Json.put(destination, "ipaddr", ipaddr);

            Json.put(message, "destination", destination);
        }

        TPL.instance.message.sendMessage(message);
    }

    public static void sendAllLEDOnOff(boolean on)
    {
        sendLEDOnOff(null, on);
    }

    public static void sendLEDOnOff(String ipaddr, boolean on)
    {
        String messOn = "{\"system\":{\"set_led_off\":{\"off\": 0}}}";
        String messOff = "{\"system\":{\"set_led_off\":{\"off\": 1}}}";

        JSONObject message = Json.fromStringObject(on ? messOn : messOff);

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
        //Log.d(LOGTAG, Json.toPretty(message));

        JSONObject origin = Json.getObject(message, "origin");
        String ipaddr = Json.getString(origin, "ipaddr");

        //
        // Request sys info.
        //

        TPLHandlerSysInfo.sendGetSysinfo(ipaddr);
    }
}
