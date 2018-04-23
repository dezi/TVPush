package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.iot.handler.IOTHandleStot;
import de.xavaro.android.pub.interfaces.pub.PUBSpeechListener;
import de.xavaro.android.pub.interfaces.pub.PUBSmartBulb;
import de.xavaro.android.pub.interfaces.pub.PUBSmartPlug;
import de.xavaro.android.pub.interfaces.pub.PUBCamera;

import de.xavaro.android.gui.simple.Json;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.iam.base.IAM;
import de.xavaro.android.spr.base.SPR;
import de.xavaro.android.tpl.base.TPL;
import de.xavaro.android.brl.base.BRL;
import de.xavaro.android.edx.base.EDX;

import zz.top.p2p.base.P2P;

public class SystemsGUI extends GUI
{
    private static final String LOGTAG = SystemsGUI.class.getSimpleName();

    private Application appcontext;

    public SystemsGUI(Application appcontext)
    {
        super(appcontext);

        this.appcontext = appcontext;
    }

    @Override
    public int getSubsystemState(String subsystem)
    {
        return GUI.instance.subSystems.getSubsystemState(subsystem);
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        GUI.instance.subSystems.setSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        GUI.instance.subSystems.setSubsystemRunstate(subsystem, state);
    }

    @Override
    public JSONObject onGetSubsystemSettings(String subsystem)
    {
        return Systems.getSubsystemSettings(appcontext, subsystem);
    }

    @Override
    public void onStartSubsystemRequest(String subsystem)
    {
        Systems.startSubsystem(appcontext, subsystem);
    }

    @Override
    public void onStopSubsystemRequest(String subsystem)
    {
        Systems.stopSubsystem(subsystem);
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: speech=" + speech.toString());

        super.onSpeechResults(speech);

        if (IAM.instance != null)
        {
            IAM.instance.evaluateSpeech(speech);
        }
        else
        {
            if (! Json.has(speech, "remote"))
            {
                Json.put(speech, "remote", true);

                IOTHandleStot.sendSTOT(speech);
            }
        }
    }

    @Override
    public PUBSpeechListener onSpeechListenerHandlerRequest()
    {
        if (SPR.instance != null)
        {
            return SPR.instance.getSpeechListenerHandler();
        }

        return null;
    }

    @Override
    public PUBCamera onCameraHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String driver = Json.getString(device, "driver");

        Log.d(LOGTAG, "onCameraHandlerRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return null;

        if (driver.equals("p2p") && (P2P.instance != null))
        {
            return P2P.instance.getCameraHandler(device, status, credentials);
        }

        return null;
    }

    @Override
    public PUBSmartPlug onSmartPlugHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String driver = Json.getString(device, "driver");

        Log.d(LOGTAG, "OnSmartPlugHandlerRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return null;

        if (driver.equals("brl") && (BRL.instance != null))
        {
            return BRL.instance.getSmartPlugHandler(device, status, credentials);
        }

        if (driver.equals("tpl") && (TPL.instance != null))
        {
            return TPL.instance.getSmartPlugHandler(device, status, credentials);
        }

        if (driver.equals("edx") && (EDX.instance != null))
        {
            return EDX.instance.getSmartPlugHandler(device, status, credentials);
        }

        return null;
    }

    @Override
    public PUBSmartBulb onSmartBulbHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String driver = Json.getString(device, "driver");

        Log.d(LOGTAG, "onSmartBulbHandlerRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return null;

        if (driver.equals("tpl") && (TPL.instance != null))
        {
            return TPL.instance.getSmartBulbHandler(device, status, credentials);
        }

        return null;
    }
}
