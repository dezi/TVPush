package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;

import de.xavaro.android.iam.base.IAM;
import pub.android.interfaces.cam.Camera;

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

        return null;
    }
}
