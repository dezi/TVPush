package zz.top.tpl.comm;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import zz.top.tpl.base.TPL;
import zz.top.tpl.handler.TPLHandler;
import zz.top.tpl.handler.TPLHandlerSysInfo;

import zz.top.utl.Json;

public class TPLMessageHandler
{
    private static final String LOGTAG = TPLMessageHandler.class.getSimpleName();

    public static void initialize()
    {
        TPL.message = new TPLMessageHandler();
        TPL.message.initializeBasicSubscribers();
    }

    private final Map<String, ArrayList<TPLHandler>> subscribers = new HashMap<>();

    private void initializeBasicSubscribers()
    {
        subscribe("get_sysinfo", new TPLHandlerSysInfo());
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
        String type = TPLUDP.getMessageType(message);

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
