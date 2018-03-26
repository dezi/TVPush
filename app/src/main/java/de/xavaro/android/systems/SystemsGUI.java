package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.simple.Json;

import de.xavaro.android.iam.base.IAM;

import pub.android.interfaces.drv.Camera;
import pub.android.interfaces.drv.SmartBulb;
import pub.android.interfaces.drv.SmartPlug;

public class SystemsGUI extends GUI
{
    private static final String LOGTAG = SystemsGUI.class.getSimpleName();

    public SystemsGUI(Application application)
    {
        super(application);
    }

    @Override
    public void onStartSubsystemRequest(String drv)
    {
        if (drv.equals("iam"))
        {
            SystemsIAM.instance.startSubsystem();
        }
    }

    @Override
    public void onStopSubsystemRequest(String drv)
    {
        if (drv.equals("iam"))
        {
            SystemsIAM.instance.stopSubsystem();
        }
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: speech=" + speech.toString());

        IAM.instance.evaluateSpeech(speech);
    }

    @Override
    public Camera onCameraHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String driver = Json.getString(device, "driver");

        Log.d(LOGTAG, "onCameraHandlerRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return null;

        if (driver.equals("p2p"))
        {
            return SystemsP2P.instance.getCameraHandler(device, status, credentials);
        }

        return null;
    }

    @Override
    public SmartPlug onSmartPlugHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String driver = Json.getString(device, "driver");

        Log.d(LOGTAG, "OnSmartPlugHandlerRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return null;

        if (driver.equals("tpl"))
        {
            return SystemsTPL.instance.getSmartPlugHandler(device, status, credentials);
        }

        return null;
    }

    @Override
    public SmartBulb onSmartBulbHandlerRequest(JSONObject device, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(device, "uuid");
        String driver = Json.getString(device, "driver");

        Log.d(LOGTAG, "onSmartBulbHandlerRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return null;

        if (driver.equals("tpl"))
        {
            return SystemsTPL.instance.getSmartBulbHandler(device, status, credentials);
        }

        return null;
    }
}
