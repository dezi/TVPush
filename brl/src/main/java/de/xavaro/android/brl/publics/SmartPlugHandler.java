package de.xavaro.android.brl.publics;

import org.json.JSONObject;

import de.xavaro.android.brl.base.BRL;
import de.xavaro.android.brl.comm.BRLCommand;
import de.xavaro.android.brl.simple.Json;
import de.xavaro.android.brl.simple.Simple;
import pub.android.interfaces.pub.PUBSmartPlug;

public class SmartPlugHandler implements PUBSmartPlug
{
    private String uuid;

    private String ipaddr;
    private String macaddr;

    public SmartPlugHandler(String uuid, String ipaddr, String macaddr)
    {
        this.uuid = uuid;
        this.ipaddr = ipaddr;
        this.macaddr = macaddr;
    }

    @Override
    public boolean setPlugState(final int onoff)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                int res =  BRLCommand.setPowerStatus(ipaddr, macaddr, onoff);

                if (res >= 0)
                {
                    JSONObject status = new JSONObject();

                    Json.put(status, "uuid", uuid);
                    Json.put(status, "plugstate", res);

                    BRL.instance.onDeviceStatus(status);
                }
            }
        };

        Simple.runBackground(runnable);

        return true;
    }

    @Override
    public boolean setLEDState(int onoff)
    {
        return false;
    }
}
