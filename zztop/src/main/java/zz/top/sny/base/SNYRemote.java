package zz.top.sny.base;

import android.os.StrictMode;
import android.util.Log;

public class SNYRemote
{
    private final static String LOGTAG = SNYRemote.class.getSimpleName();

    private final static String braviaIRCCEndPoint = "http://####/sony/IRCC";

    private final static String xmlTemplate = ""
            + "<?xml version=\"1.0\"?>\n"
            + "<s:Envelope xmlns:s=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
            + "    s:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n"
            + "  <s:Body>\n"
            + "    <u:X_SendIRCC xmlns:u=\"urn:schemas-sony-com:service:IRCC:1\">\n"
            + "      <IRCCCode>####</IRCCCode>\n"
            + "    </u:X_SendIRCC>\n"
            + "  </s:Body>\n"
            + "</s:Envelope>\n"
            ;

    public static boolean sendRemoteCommand(String ipaddr, String authtoken, String action)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        String ircc = SNYActions.getAction(action);
        if (ircc == null) return false;

        Log.d(LOGTAG, "sendRemoteCommand: start action=" + action);

        String urlstr = braviaIRCCEndPoint.replace("####", ipaddr);
        String xmlBody = xmlTemplate.replace("####", ircc);

        //Log.d(LOGTAG, "sendRemoteCommand authtoken=" + authtoken);
        //Log.d(LOGTAG, "sendRemoteCommand xmlBody=" + xmlBody);

        String result = SNYUtil.getPostXML(urlstr, xmlBody, authtoken);

        try
        {
            Thread.sleep(100);
        }
        catch (Exception ignore)
        {
        }

        Log.d(LOGTAG, "sendRemoteCommand: done action=" + action);

        //Log.d(LOGTAG, "sendRemoteCommand result=" + result);

        return true;
    }
}
