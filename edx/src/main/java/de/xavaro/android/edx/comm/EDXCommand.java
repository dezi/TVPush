package de.xavaro.android.edx.comm;

import android.support.annotation.Nullable;
import android.annotation.SuppressLint;

import de.xavaro.android.edx.simple.Log;

public class EDXCommand
{
    private final static String LOGTAG = EDXCommand.class.getSimpleName();

    private final static String baseUrl = "http://%s:%d/smartplug.cgi";

    private final static String getSystemInfo = ""
            + "<?xml version='1.0' encoding='UTF8'?>\n"
            + "<SMARTPLUG id='edimax'>\n"
            + "    <CMD id='get'>\n"
            + "        <SYSTEM_INFO></SYSTEM_INFO>\n"
            + "    </CMD>\n"
            +"</SMARTPLUG>\n";

    private final static String getStatus = ""
            + "<?xml version='1.0' encoding='UTF8'?>\n"
            + "<SMARTPLUG id='edimax'>\n"
            + "    <CMD id='get'>\n"
            + "        <Device.System.Power.State></Device.System.Power.State>\n"
            + "    </CMD>\n"
            + "</SMARTPLUG>\n";

    private final static String switchOn = "<?xml version='1.0' encoding='UTF8'?>\n"
            + "<SMARTPLUG id='edimax'>\n"
            + "    <CMD id='setup'>\n"
            + "        <Device.System.Power.State>ON</Device.System.Power.State>\n"
            + "    </CMD>\n"
            + "</SMARTPLUG>\n";

    private final static String switchOff = "<?xml version='1.0' encoding='UTF8'?>\n"
            + "<SMARTPLUG id='edimax'>\n"
            + "    <CMD id='setup'>\n"
            + "        <Device.System.Power.State>OFF</Device.System.Power.State>\n"
            + "    </CMD>\n"
            + "</SMARTPLUG>\n";

    public static void getSystemInfo(String ipaddr, int ipport, String user, String pass)
    {
        execCommand(ipaddr, ipport, user, pass, getSystemInfo);
    }

    public static int getPowerStatus(String ipaddr, int ipport, String user, String pass)
    {
        String result = execCommand(ipaddr, ipport, user, pass, getStatus);

        return (result == null) ? -1 : result.contains("<Device.System.Power.State>ON</Device.System.Power.State>") ? 1 : 0;
    }

    public static int setPowerStatus(String ipaddr, int ipport, String user, String pass, int onOff)
    {
        String result = execCommand(ipaddr, ipport, user, pass, (onOff == 1) ? switchOn : switchOff);

        return ((result == null) || ! result.contains("<CMD id=\"setup\">OK</CMD>")) ? -1 : onOff;
    }

    @Nullable
    @SuppressLint("DefaultLocale")
    private static String execCommand(String ipaddr, int ipport, String user, String pass, String xml)
    {
        String url = String.format(baseUrl, ipaddr, ipport);

        Log.d(LOGTAG, "execCommand:"
                + " ipaddr=" + ipaddr
                + " ipport=" + ipport
                + " user=" + user
                + " pass=" + pass);

        Log.d(LOGTAG, "execCommand: xml=" + xml);

        String result = EDXPostDevice.getPost(url, xml, user, pass);

        if (result != null)
        {
            result = result.replaceAll("></", ">@@</");
            result = result.replaceAll("><", ">\n<");
            result = result.replaceAll(">@@</", "></");
        }

        //Log.d(LOGTAG, "execCommand: result=" + result);

        return result;
    }
}
