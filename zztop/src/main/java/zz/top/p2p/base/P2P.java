package zz.top.p2p.base;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;
import pub.android.interfaces.drv.Camera;
import pub.android.interfaces.ext.GetCameraHandler;
import pub.android.interfaces.ext.OnDeviceHandler;
import pub.android.interfaces.all.DoSomethingHandler;

import zz.top.p2p.camera.P2PCamera;
import zz.top.p2p.camera.P2PCloud;
import zz.top.p2p.camera.P2PSession;
import zz.top.p2p.commands.CloseCameraSend;
import zz.top.p2p.commands.LEDOnOffSend;
import zz.top.utl.Json;
import zz.top.utl.Simple;

public class P2P implements
        SubSystemHandler,
        OnDeviceHandler,
        DoSomethingHandler,
        GetCameraHandler
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

    @Override
    public JSONObject getSubsystemInfo()
    {
        JSONObject info = new JSONObject();

        Json.put(info, "drv", "p2p");
        Json.put(info, "name", "YI Home Cameras");

        return info;
    }

    @Override
    public void startSubsystem()
    {
    }

    @Override
    public void stopSubsystem()
    {
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStarted: STUB! state=" + state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        Log.d(LOGTAG, "onSubsystemStopped: STUB! state=" + state);
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
    public void onDeviceMetadata(JSONObject metadata)
    {
        Log.d(LOGTAG, "onDeviceMetadata: STUB!");
    }

    @Override
    public void onDeviceCredentials(JSONObject credentials)
    {
        Log.d(LOGTAG, "onDeviceCredentials: STUB!");
    }

    @Override
    public Camera getCameraHandler(JSONObject device, JSONObject status, JSONObject credentials)
    {
        return new P2PCamera(device, credentials);
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
            if (actioncmd.equals("activate") || actioncmd.equals("deactivate"))
            {
                boolean close = actioncmd.equals("deactivate");

                P2PSession session = new P2PSession();
                session.attachCamera(uuid, p2p_id, p2p_pw);

                if (session.connect())
                {
                    (new LEDOnOffSend(session, ! close)).send();
                    (new CloseCameraSend(session, close)).send();

                    //session.disconnect();
                }

                return true;
            }

            if (actioncmd.equals("switchonled") || actioncmd.equals("switchoffled"))
            {
                boolean onoff = actioncmd.equals("switchonled");

                P2PSession session = new P2PSession();
                session.attachCamera(uuid, p2p_id, p2p_pw);

                if (session.connect())
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
