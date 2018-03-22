package de.xavaro.android.systems;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;

import de.xavaro.android.iam.base.IAM;
import pub.android.interfaces.drv.Camera;
import pub.android.interfaces.drv.SmartPlug;
import zz.top.cam.Cameras;
import zz.top.utl.Json;

public class SystemsGUI extends GUI
{
    private static final String LOGTAG = SystemsGUI.class.getSimpleName();

    public SystemsGUI(Application application)
    {
        super(application);
    }

    @Override
    public void onSpeechResults(JSONObject speech)
    {
        Log.d(LOGTAG, "onSpeechResults: speech=" + speech.toString());

        IAM.instance.evaluateSpeech(speech);
    }

    @Override
    public Camera onRequestCameraByUUID(String uuid)
    {
        Log.d(LOGTAG, "onRequestCameraByUUID: uuid=" + uuid);

        return Cameras.createCameraByUUID(uuid);
    }

    @Override
    public SmartPlug onSmartPlugHandlerRequest(JSONObject iotDevice, JSONObject status, JSONObject credentials)
    {
        String uuid = Json.getString(iotDevice, "uuid");
        String driver = Json.getString(iotDevice, "driver");

        Log.d(LOGTAG, "OnSmartPlugHandlerRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return null;

        if (driver.equals("tpl"))
        {
            return SystemsTPL.instance.getSmartPlugHandler(iotDevice, status, credentials);
        }

        return null;
    }
}
