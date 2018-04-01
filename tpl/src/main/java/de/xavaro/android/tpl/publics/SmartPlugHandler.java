package de.xavaro.android.tpl.publics;

import pub.android.interfaces.pub.PUBSmartPlug;
import de.xavaro.android.tpl.handler.TPLHandlerSmartPlug;

public class SmartPlugHandler implements PUBSmartPlug
{
    private String ipaddr;

    public SmartPlugHandler(String ipaddr)
    {
        this.ipaddr = ipaddr;
    }

    @Override
    public boolean setPlugState(int onoff)
    {
        TPLHandlerSmartPlug.sendPlugOnOff(ipaddr, (onoff == 1));

        return true;
    }

    @Override
    public boolean setLEDState(int onoff)
    {
        TPLHandlerSmartPlug.sendLEDOnOff(ipaddr,  (onoff == 1));

        return false;
    }
}
