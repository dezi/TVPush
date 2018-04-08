package de.xavaro.android.edx.comm;

import de.xavaro.android.edx.simple.Log;

public class EDXCommand
{
    private static final String LOGTAG = EDXCommand.class.getSimpleName();

    private static final String baseUrl = "http://%s:%d/smartplug.cgi";

    private static String setName = ""
            + "<?xml version='1.0' encoding='UTF-8'?>\n"
            + "<SMARTPLUG id='edimax'>\n"
            + "    <CMD id='setup'>\n"
            + "        <SYSTEM_INFO>\n"
            + "            <Device.System.Name>%s</Device.System.Name>\n"
            + "        </SYSTEM_INFO>\n"
            + "    </CMD>\n"
            + "</SMARTPLUG>\n";

    public static void setName(String ipaddr, int ipport, String name)
    {
        String url = String.format(baseUrl, ipaddr, ipport);
        String xml = String.format(setName, name);

        Log.d(LOGTAG, "setName: xml=" + xml);

        String result = EDXUtil.getPost(url, xml, null, "admin", "hallo1234");

        Log.d(LOGTAG, "setName: result=" + result);
    }

    private static String getSystemInfo = ""
            + "<?xml version='1.0' encoding='UTF8'?>\n"
            + "<SMARTPLUG id='edimax'>\n"
            + "    <CMD id='get'>\n"
            + "        <SYSTEM_INFO></SYSTEM_INFO>\n"
            + "    </CMD>\n"
            +"</SMARTPLUG>\n";

    public static void getSystemInfo(String ipaddr, int ipport)
    {
        String url = String.format(baseUrl, ipaddr, ipport);
        String xml = getSystemInfo;

        Log.d(LOGTAG, "getSystemInfo: xml=" + xml);

        String result = EDXUtil.getPost(url, xml, null, "admin", "hallo1234");

        Log.d(LOGTAG, "getSystemInfo: result=" + result);
    }
}
