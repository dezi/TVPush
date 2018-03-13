package zz.top.p2p.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import pub.android.interfaces.iot.InternetOfThingsHandler;

import zz.top.p2p.camera.P2PCloud;
import zz.top.p2p.camera.P2PSession;
import zz.top.p2p.commands.LEDOnOffSend;
import zz.top.tpl.handler.TPLHandlerSmartPlug;
import zz.top.utl.Json;
import zz.top.utl.Simple;

public class P2P implements InternetOfThingsHandler
{
    private static final String LOGTAG = P2P.class.getSimpleName();

    public static P2P instance;

    public P2PCloud cloud;

    public P2P(Application application)
    {
        instance = this;

        Simple.initialize(application);

        cloud = new P2PCloud(this);
    }

    public void login(String email, String password)
    {
        cloud.login(email, password);
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound: STUB!");
    }

    @Override
    public void onDeviceStatus(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceStatus: STUB!");
    }

    @Override
    public boolean doSomething(JSONObject action, JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(status, "uuid");
        if (uuid != null) cloud.statusCache.put(uuid, status);

        Log.d(LOGTAG, "doSomething: action=" + Json.toPretty(action));
        Log.d(LOGTAG, "doSomething: credentials=" + Json.toPretty(credentials));

        JSONObject mycredentials = Json.getObject(credentials, "credentials");

        String p2p_id = Json.getString(mycredentials, "p2p_id");
        String p2p_pw = Json.getString(mycredentials, "p2p_pw");

        String actioncmd = Json.getString(action, "action");

        if ((actioncmd != null) && (p2p_id != null) && (p2p_pw != null))
        {
            if (actioncmd.equals("switchonled") || actioncmd.equals("switchoffled"))
            {
                boolean onoff = actioncmd.equals("switchonled");

                P2PSession session = new P2PSession();
                session.attachCamera(uuid, p2p_id, p2p_pw);

                if (session.connect(true))
                {
                    (new LEDOnOffSend(session, onoff)).send();

                    //session.disconnect();
                }

                return true;
            }
        }

        return false;
    }
}
