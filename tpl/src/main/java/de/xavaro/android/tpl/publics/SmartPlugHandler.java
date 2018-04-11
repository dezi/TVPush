package de.xavaro.android.tpl.publics;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBSmartPlug;

import de.xavaro.android.tpl.handler.TPLHandlerSmartPlug;
import de.xavaro.android.tpl.simple.Simple;
import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.base.TPL;

public class SmartPlugHandler implements PUBSmartPlug
{
    private String uuid;
    private String ipaddr;

    public SmartPlugHandler(String uuid, String ipaddr)
    {
        this.uuid = uuid;
        this.ipaddr = ipaddr;
    }

    @Override
    public boolean setPlugState(final int onoff)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (TPLHandlerSmartPlug.sendPlugOnOff(ipaddr, onoff) >= 0)
                {
                    JSONObject status = new JSONObject();

                    Json.put(status, "uuid", uuid);
                    Json.put(status, "plugstate", onoff);

                    TPL.instance.onDeviceStatus(status);
                }
            }
        };

        Simple.runBackground(runnable);

        return true;
    }

    @Override
    public boolean setLEDState(final int onoff)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (TPLHandlerSmartPlug.sendLEDOnOff(ipaddr, onoff) >= 0)
                {
                    JSONObject status = new JSONObject();

                    Json.put(status, "uuid", uuid);
                    Json.put(status, "ledstate", onoff);

                    TPL.instance.onDeviceStatus(status);
                }
            }
        };

        Simple.runBackground(runnable);

        return true;
    }
}
