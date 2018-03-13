package zz.top.sny.base;

import android.util.Log;

public class SNYRemote
{
    private final static String LOGTAG = SNYRemote.class.getSimpleName();

    private final static String braviaIRCCEndPoint = "http://####/sony/IRCC";

    private final static String xmlTemplate = ""
            + "<?xml version=\"1.0\"?>"
            + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\" s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">"
            + "  <s:Body>"
            + "    <u:X_SendIRCC xmlns:u=\"urn:schemas-sony-com:service:IRCC:1\">"
            + "      <IRCCCode>####</IRCCCode>"+ ""
            + "    </u:X_SendIRCC>"
            + "  </s:Body>"
            + "</s:Envelope>"
            ;

    public static boolean sendRemoteCommand(String ipaddr, String authcookie, String action)
    {
        String ircc = SNYActions.getAction(action);
        if (ircc == null) return false;

        String urlstr = braviaIRCCEndPoint.replace("####", ipaddr);
        String xmlBody = xmlTemplate.replace("####", ircc);

        Log.d(LOGTAG, "sendRemoteCommand xmlBody=" + xmlBody);

        String result = SNYUtil.getPostXML(urlstr, xmlBody, authcookie);

        Log.d(LOGTAG, "sendRemoteCommand result=" + result);

        return true;
    }
}
