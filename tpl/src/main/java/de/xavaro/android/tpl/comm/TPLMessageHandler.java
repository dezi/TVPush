package de.xavaro.android.tpl.comm;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.tpl.handler.TPLHandlerSmartBulb;
import de.xavaro.android.tpl.handler.TPLHandlerSmartPlug;
import de.xavaro.android.tpl.handler.TPLHandlerSysInfo;
import de.xavaro.android.tpl.handler.TPLHandler;
import de.xavaro.android.tpl.base.TPL;

public class TPLMessageHandler
{
    private static final String LOGTAG = TPLMessageHandler.class.getSimpleName();

    private final Map<String, ArrayList<TPLHandler>> subscribers = new HashMap<>();

    public static void startService()
    {
        if (TPL.instance.message == null)
        {
            TPL.instance.message = new TPLMessageHandler();
        }
    }

    public static void stopService()
    {
        TPL.instance.message = null;
    }

    public TPLMessageHandler()
    {
        initializeBasicSubscribers();
    }

    private void initializeBasicSubscribers()
    {
        subscribe("get_sysinfo", new TPLHandlerSysInfo());
        subscribe("set_led_off", new TPLHandlerSmartPlug());
        subscribe("set_relay_state", new TPLHandlerSmartPlug());
        subscribe("transition_light_state", new TPLHandlerSmartBulb());
    }

    public void subscribe(String type, TPLHandler handler)
    {
        ArrayList<TPLHandler> typeHandlers = subscribers.get(type);

        if (typeHandlers == null)
        {
            typeHandlers = new ArrayList<>();
            subscribers.put(type, typeHandlers);
        }

        if (! typeHandlers.contains(handler))
        {
            typeHandlers.add(handler);
        }
    }

    public void sendMessage(JSONObject message)
    {
        TPLMessageService.sendMessage(message);
    }

    public void receiveMessage(JSONObject message)
    {
        String type = TPLDatagrammService.getMessageType(message);

        if (type == null) return;

        //
        // Call type handlers.
        //

        ArrayList<TPLHandler> typeHandlers = subscribers.get(type);
        if (typeHandlers == null) return;

        for (TPLHandler typeHandler : typeHandlers)
        {
            typeHandler.onMessageReived(message);
        }
    }
}
