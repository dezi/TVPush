package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.adb.base.ADB;
import de.xavaro.android.bcn.base.BCN;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.simple.Json;

import de.xavaro.android.iam.base.IAM;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.spr.base.SPR;
import pub.android.interfaces.pub.PUBCamera;
import pub.android.interfaces.pub.PUBSmartBulb;
import pub.android.interfaces.pub.PUBSmartPlug;

import zz.top.p2p.base.P2P;
import de.xavaro.android.sny.base.SNY;
import de.xavaro.android.tpl.base.TPL;

public class SystemsGUI extends GUI
{
    private static final String LOGTAG = SystemsGUI.class.getSimpleName();

    private Application appcontext;

    public SystemsGUI(Application appcontext)
    {
        super(appcontext);

        this.appcontext = appcontext;

        subSystems.registerSubsystem(getSubsystemInfo());
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
    public void onStartSubsystemRequest(String subsystem)
    {
        if (subsystem.startsWith("adb"))
        {
            if (ADB.instance == null) ADB.instance = new SystemsADB(appcontext);

            ADB.instance.startSubsystem();
        }

        if (subsystem.startsWith("gui"))
        {
            if (GUI.instance == null) GUI.instance = new SystemsGUI(appcontext);

            GUI.instance.startSubsystem();
        }

        if (subsystem.startsWith("iot"))
        {
            if (IOT.instance == null) IOT.instance = new SystemsIOT(appcontext);

            IOT.instance.startSubsystem();
        }

        if (subsystem.startsWith("iam"))
        {
            if (IAM.instance == null) IAM.instance = new SystemsIAM(appcontext);

            IAM.instance.startSubsystem();
        }

        if (subsystem.startsWith("bcn"))
        {
            if (BCN.instance == null) BCN.instance = new SystemsBCN(appcontext);

            BCN.instance.startSubsystem();
        }

        if (subsystem.startsWith("sny"))
        {
            if (SNY.instance == null) SNY.instance = new SystemsSNY(appcontext);

            SNY.instance.startSubsystem();
        }

        if (subsystem.startsWith("spr"))
        {
            if (SPR.instance == null) SPR.instance = new SystemsSPR(appcontext);

            SPR.instance.startSubsystem();
        }

        if (subsystem.startsWith("tpl"))
        {
            if (TPL.instance == null) TPL.instance = new SystemsTPL(appcontext);

            TPL.instance.startSubsystem();
        }

        if (subsystem.startsWith("p2p"))
        {
            if (P2P.instance == null) P2P.instance = new SystemsP2P(appcontext);

            P2P.instance.startSubsystem();
        }
    }

    @Override
    public void onStopSubsystemRequest(String subsystem)
    {
        if (subsystem.startsWith("adb"))
        {
            if (ADB.instance != null)
            {
                ADB.instance.stopSubsystem();

                if (subsystem.equals("adb")) ADB.instance = null;
            }
        }

        if (subsystem.startsWith("gui"))
        {
            if (GUI.instance != null)
            {
                GUI.instance.stopSubsystem();

                if (subsystem.equals("GUI")) GUI.instance = null;
            }
        }

        if (subsystem.startsWith("iot"))
        {
            if (IOT.instance != null)
            {
                IOT.instance.stopSubsystem();

                if (subsystem.equals("IOT")) IOT.instance = null;
            }
        }

        if (subsystem.startsWith("iam"))
        {
            if (IAM.instance != null)
            {
                IAM.instance.stopSubsystem();

                if (subsystem.equals("iam")) IAM.instance = null;
            }
        }

        if (subsystem.startsWith("bcn"))
        {
            if (BCN.instance != null)
            {
                BCN.instance.stopSubsystem();

                if (subsystem.equals("bcn")) BCN.instance = null;
            }
        }

        if (subsystem.startsWith("spr"))
        {
            if (SPR.instance != null)
            {
                SPR.instance.stopSubsystem();

                if (subsystem.equals("spr")) SPR.instance = null;
            }
        }

        if (subsystem.startsWith("sny"))
        {
            if (SNY.instance != null)
            {
                SNY.instance.stopSubsystem();

                if (subsystem.equals("sny")) SNY.instance = null;
            }
        }

        if (subsystem.startsWith("tpl"))
        {
            if (TPL.instance != null)
            {
                TPL.instance.stopSubsystem();

                if (subsystem.equals("tpl")) TPL.instance = null;
            }
        }

        if (subsystem.startsWith("p2p"))
        {
            if (P2P.instance != null)
            {
                P2P.instance.stopSubsystem();
                if (subsystem.equals("p2p")) P2P.instance = null;
            }
        }
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
