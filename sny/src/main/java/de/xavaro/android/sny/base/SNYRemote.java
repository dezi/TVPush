package de.xavaro.android.sny.base;

import org.json.JSONObject;

import java.util.ArrayList;

import de.xavaro.android.sny.simple.Json;
import de.xavaro.android.sny.simple.Log;

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

    public static void startService()
    {
        if ((SNY.instance != null) && (SNY.instance.remote == null))
        {
            SNY.instance.remote = new SNYRemote();
        }
    }

    public static void stopService()
    {
        if ((SNY.instance != null) && (SNY.instance.remote != null))
        {
            SNYRemote remote = SNY.instance.remote;

            synchronized (remote.mutex)
            {
                if (remote.remoteThread != null)
                {
                    remote.remoteThread.interrupt();
                    remote.remoteThread = null;
                }
            }

            SNY.instance.remote = null;
        }
    }

    private final Object mutex = new Object();
    private final ArrayList<JSONObject> queue;

    private Thread remoteThread;

    private SNYRemote()
    {
        queue = new ArrayList<>();

        try
        {
            remoteThread = new Thread(remoteRunnable);
            remoteThread.start();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private final Runnable remoteRunnable = new Runnable()
    {
        @Override
        public void run()
        {
            while (remoteThread != null)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (Exception ignore)
                {
                }

                JSONObject json = null;

                synchronized (queue)
                {
                    if (queue.size() > 0)
                    {
                        json = queue.remove(0);
                    }
                }

                if (json == null) continue;

                sendRemoteCommand(json);
            }
        }
    };

    private void sendRemoteCommand(JSONObject json)
    {
        String ipaddr = Json.getString(json, "ipaddr");
        String authtoken = Json.getString(json,"authtoken");
        String action = Json.getString(json, "action");
        String ircc = Json.getString(json, "ircc");

        if ((ipaddr == null) || (ircc == null)) return;

        Log.d(LOGTAG, "sendRemoteCommand: start action=" + action);

        String urlstr = braviaIRCCEndPoint.replace("####", ipaddr);
        String xmlBody = xmlTemplate.replace("####", ircc);

        Log.d(LOGTAG, "sendRemoteCommand authtoken=" + authtoken);
        Log.d(LOGTAG, "sendRemoteCommand xmlBody=" + xmlBody);

        String result = SNYUtil.getPostXML(urlstr, xmlBody, authtoken);

        Log.d(LOGTAG, "sendRemoteCommand: done action=" + action);
        Log.d(LOGTAG, "sendRemoteCommand result=" + result);
    }

    public boolean sendRemoteCommand(String ipaddr, String authtoken, String action)
    {
        String ircc = SNYActions.getAction(action);
        if (ircc == null) return false;

        JSONObject json = new JSONObject();
        Json.put(json, "ipaddr", ipaddr);
        Json.put(json, "authtoken", authtoken);
        Json.put(json, "action", action);
        Json.put(json, "ircc", ircc);

        synchronized (queue)
        {
            queue.add(json);
        }

        return true;
    }

}
