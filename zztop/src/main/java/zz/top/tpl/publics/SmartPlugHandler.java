package zz.top.tpl.publics;

import pub.android.interfaces.drv.SmartPlug;
import zz.top.tpl.handler.TPLHandlerSmartPlug;

public class SmartPlugHandler implements SmartPlug
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
