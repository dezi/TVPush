package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iam.base.IAM;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

import zz.top.utl.Json;

public class SystemsIAM extends IAM
{
    private static final String LOGTAG = SystemsIAM.class.getSimpleName();

    public SystemsIAM(Application application)
    {
        super(application);
    }

    @Override
    public void onActionsFound(JSONArray actions)
    {
        if ((actions == null) || (actions.length() == 0))
        {
            Log.d(LOGTAG, "onActionsFound: no actions found.");
            return;
        }

        Log.d(LOGTAG, "onActionsFound: actions=");
        Log.d(LOGTAG, Json.toPretty(actions));

        for (int inx = 0; inx < actions.length(); inx++)
        {
            JSONObject jaction = Json.getObject(actions, inx);
            if (jaction == null) continue;

            String action = Json.getString(jaction, "action");
            if ((action == null) || action.isEmpty()) continue;

            String object = Json.getString(jaction, "object");

            if ((object != null) && ! object.isEmpty())
            {
                if (object.equals("speechlistener"))
                {
                    if (action.equals("open"))
                    {
                        Systems.gui.instance.displaySpeechRecognition(true);
                        continue;
                    }

                    if (action.equals("close"))
                    {
                        Systems.gui.instance.displaySpeechRecognition(false);
                        continue;
                    }
                }
            }

            JSONArray devices = Json.getArray(jaction, "devices");

            if (devices != null)
            {
                for (int dinx = 0; dinx < devices.length(); dinx++)
                {
                    String uuid = Json.getString(devices, dinx);
                    if ((uuid == null) || uuid.isEmpty()) continue;

                    IOTDevice device = IOTDevices.getEntry(uuid);
                    if ((device == null) || (device.type == null)) continue;

                    if (device.type.equals("camera"))
                    {
                        if (action.equals("open"))
                        {
                            Systems.gui.instance.displayCamera(true, uuid);
                            continue;
                        }

                        if (action.equals("close"))
                        {
                            Systems.gui.instance.displayCamera(false, uuid);
                            continue;
                        }
                    }

                    if (action.equals("switchonled"))
                    {
                        if (device.driver.equals("p2p"))
                        {
                            Systems.p2p.switchLED(uuid, true);
                        }

                        if (device.driver.equals("tpl"))
                        {
                            Systems.tpl.switchLED(uuid, true);
                        }
                    }

                    if (action.equals("switchoffled"))
                    {
                        if (device.driver.equals("p2p"))
                        {
                            Systems.p2p.switchLED(uuid, false);
                        }

                        if (device.driver.equals("tpl"))
                        {
                            Systems.tpl.switchLED(uuid, false);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void evaluateSpeech(JSONObject speech)
    {
        Log.d(LOGTAG, "evaluateSpeech: speech=" + speech.toString());

        super.evaluateSpeech(speech);
    }
}
