package de.xavaro.android.adb.publics;

import pub.android.interfaces.pub.PUBADBTool;

import de.xavaro.android.adb.conn.AdbTest;

public class ADBToolHandler implements PUBADBTool
{
    private String ipaddr;
    private int ipport;

    public ADBToolHandler(String ipaddr)
    {
        this.ipaddr = ipaddr;
        this.ipport = 5555;
    }

    public boolean isConfigured()
    {
        return AdbTest.getADBConfigured(ipaddr, ipport);
    }
}
