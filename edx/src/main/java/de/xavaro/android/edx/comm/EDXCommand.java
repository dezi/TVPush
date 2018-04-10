package de.xavaro.android.edx.comm;

import org.json.JSONObject;

import de.xavaro.android.edx.simple.Log;

public class EDXCommand
{
    private static final String LOGTAG = EDXCommand.class.getSimpleName();

    private static final String baseUrl = "http://%s:%d/smartplug.cgi";

    private static String getSystemInfo = ""
            + "<?xml version='1.0' encoding='UTF8'?>\n"
            + "<SMARTPLUG id='edimax'>\n"
            + "    <CMD id='get'>\n"
            + "        <SYSTEM_INFO></SYSTEM_INFO>\n"
            + "    </CMD>\n"
            +"</SMARTPLUG>\n";

    public static void getSystemInfo(String ipaddr, int ipport, String user, String pass)
    {
        String url = String.format(baseUrl, ipaddr, ipport);
        String xml = getSystemInfo;

        Log.d(LOGTAG, "getSystemInfo: xml=" + xml);

        String result = EDXPostDevice.getPost(url, xml, new JSONObject(), user, pass);

        if (result != null)
        {
            result = result.replaceAll("></", ">@@</");
            result = result.replaceAll("><", ">\n<");
            result = result.replaceAll(">@@</", "></");
        }

        Log.d(LOGTAG, "getSystemInfo: result=" + result);
    }
}
