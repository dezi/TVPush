package de.xavaro.android.edx.publics;

import org.json.JSONObject;

import de.xavaro.android.edx.simple.Simple;
import de.xavaro.android.pub.interfaces.pub.PUBSmartPlug;

import de.xavaro.android.edx.comm.EDXCommand;
import de.xavaro.android.edx.base.EDX;

import de.xavaro.android.edx.simple.Json;

public class SmartPlugHandler implements PUBSmartPlug
{
    private String uuid;

    private String ipaddr;
    private int ipport;

    private String user;
    private String pass;

    public SmartPlugHandler(String uuid, String ipaddr, int ipport, String user, String pass)
    {
        this.uuid = uuid;

        this.ipaddr = ipaddr;
        this.ipport = ipport;

        this.user = user;
        this.pass = pass;
    }

    @Override
    public boolean setPlugState(final int onoff)
    {
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                int res = EDXCommand.setPowerStatus(ipaddr, ipport, user, pass, onoff);

                if (res >= 0)
                {
                    JSONObject status = new JSONObject();

                    Json.put(status, "uuid", uuid);
                    Json.put(status, "plugstate", res);

                    EDX.instance.onDeviceStatus(status);
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
