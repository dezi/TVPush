package de.xavaro.android.tpl.publics;

import pub.android.interfaces.pub.PUBSmartBulb;
import de.xavaro.android.tpl.handler.TPLHandlerSmartBulb;

public class SmartBulbHandler implements PUBSmartBulb
{
    private String ipaddr;

    public SmartBulbHandler(String ipaddr)
    {
        this.ipaddr = ipaddr;
    }

    @Override
    public boolean setBulbState(int onoff)
    {
        TPLHandlerSmartBulb.sendBulbOnOff(ipaddr, (onoff == 1));

        return true;
    }
}
