package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.bcn.base.BCN;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.simple.Json;

import de.xavaro.android.iam.base.IAM;

import pub.android.interfaces.pub.PUBCamera;
import pub.android.interfaces.pub.PUBSmartBulb;
import pub.android.interfaces.pub.PUBSmartPlug;

import zz.top.p2p.base.P2P;
import de.xavaro.android.sny.base.SNY;
import de.xavaro.android.tpl.base.TPL;

public class SystemsGUI extends GUI
{
    private static final String LOGTAG = SystemsGUI.class.getSimpleName();

    Application appcontext;

    public SystemsGUI(Application appcontext)
    {
        super(appcontext);

        this.appcontext = appcontext;

        GUI.instance.subSystems.registerSubsystem(getSubsystemInfo());
    }

    @Override
    public void onSubsystemStarted(String subsystem, int state)
    {
        GUI.instance.subSystems.registerSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onSubsystemStopped(String subsystem, int state)
    {
        GUI.instance.subSystems.registerSubsystemRunstate(subsystem, state);
    }

    @Override
    public void onStartSubsystemRequest(String drv)
    {
        if (drv.equals("iam"))
        {
            if (IAM.instance == null)
            {
                IAM.instance = new SystemsIAM(appcontext);
                IAM.instance.startSubsystem();
            }
        }

        if (drv.equals("bcn"))
        {
            if (BCN.instance == null)
            {
                BCN.instance = new SystemsBCN(appcontext);
                BCN.instance.startSubsystem();
            }
        }

        if (drv.equals("sny"))
        {
            if (SNY.instance == null)
            {
                SNY.instance = new SystemsSNY(appcontext);
                SNY.instance.startSubsystem();
            }
        }

        if (drv.equals("tpl"))
        {
            if (TPL.instance == null)
            {
                TPL.instance = new SystemsTPL(appcontext);
                TPL.instance.startSubsystem();
            }
        }

        if (drv.equals("p2p"))
        {
            if (P2P.instance == null)
            {
                P2P.instance = new SystemsP2P(appcontext);
                P2P.instance.startSubsystem();
            }
        }
    }

    @Override
    public void onStopSubsystemRequest(String drv)
    {
        if (drv.equals("iam"))
        {
            if (IAM.instance != null)
            {
                IAM.instance.stopSubsystem();
                IAM.instance = null;
            }
        }

        if (drv.equals("sny"))
        {
            if (SNY.instance != null)
            {
                SNY.instance.stopSubsystem();
                SNY.instance = null;
            }
        }

        if (drv.equals("tpl"))
        {
            if (TPL.instance != null)
            {
                TPL.instance.stopSubsystem();
                TPL.instance = null;
            }
        }

        if (drv.equals("p2p"))
        {
            if (P2P.instance != null)
            {
                P2P.instance.stopSubsystem();
                P2P.instance = null;
            }
        }
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: speech=" + speech.toString());

        if (IAM.instance != null)
        {
            IAM.instance.evaluateSpeech(speech);
        }
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

        if (driver.equals("tpl") && (TPL.instance != null))
        {
            return TPL.instance.getSmartPlugHandler(device, status, credentials);
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
