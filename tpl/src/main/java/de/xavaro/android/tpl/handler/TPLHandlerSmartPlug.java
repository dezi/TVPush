package de.xavaro.android.tpl.handler;

public class TPLHandlerSmartPlug extends TPLHandler
{
    public static int sendPlugOnOff(String ipaddr, int onOff)
    {
        String messOn = "{\"system\":{\"set_relay_state\":{\"state\":1}}}";
        String messOff = "{\"system\":{\"set_relay_state\":{\"state\":0}}}";

        String result = sendToSocket(ipaddr, (onOff == 1) ? messOn : messOff);

        return ((result == null) || ! result.contains("{\"err_code\":0}")) ? -1 : onOff;
    }

    public static int sendLEDOnOff(String ipaddr, int onOff)
    {
        String messOn = "{\"system\":{\"set_led_off\":{\"off\": 0}}}";
        String messOff = "{\"system\":{\"set_led_off\":{\"off\": 1}}}";

        String result = sendToSocket(ipaddr, (onOff == 1) ? messOn : messOff);

        return ((result == null) || ! result.contains("{\"err_code\":0}")) ? -1 : onOff;
    }
}
