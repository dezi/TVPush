package de.xavaro.android.edx.publics;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBSmartPlug;

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
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                if (EDXCommand.setPowerStatus(ipaddr, ipport, user, pass, (onoff == 1)))
                {
                    JSONObject status = new JSONObject();

                    Json.put(status, "uuid", uuid);
                    Json.put(status, "plugstate", onoff);

                    EDX.instance.onDeviceStatus(status);
                }
            }
        });

        thread.start();

        return true;
    }

    @Override
    public boolean setLEDState(int onoff)
    {
        return false;
    }
}
