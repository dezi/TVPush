package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.iam.base.IAM;
import de.xavaro.android.iam.eval.IAMEvalChannels;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTMetadata;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

import zz.top.utl.Json;

public class SystemsIAM extends IAM
{
    private static final String LOGTAG = SystemsIAM.class.getSimpleName();

    public SystemsIAM(Application application)
    {
        super(application);

        JSONObject channelsForDevices = new JSONObject();

        JSONArray tvremotes = SystemsIOT.instance.getDeviceWithCapability("tvremote");

        for (int inx = 0; inx < tvremotes.length(); inx++)
        {
            String uuid = Json.getString(tvremotes, inx);
            if (uuid == null) continue;

            IOTMetadata metadata = new IOTMetadata(uuid);
            if (metadata.metadata == null) continue;

            JSONArray PUBChannels = Json.getArray(metadata.metadata, "PUBChannels");
            if (PUBChannels == null) continue;

            Json.put(channelsForDevices, uuid, PUBChannels);
        }

        IAMEvalChannels.setChannelsForDevices(channelsForDevices);
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
                        Systems.gui.displaySpeechRecognition(true);
                        continue;
                    }

                    if (action.equals("close"))
                    {
                        Systems.gui.displaySpeechRecognition(false);
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

                    JSONObject doaction = Json.clone(jaction);
                    Json.remove(doaction, "devices");
                    Json.put(doaction, "uuid", uuid);

                    if (device.type.equals("camera"))
                    {
                        if (action.equals("open"))
                        {
                            Systems.gui.displayCamera(true, uuid);
                            continue;
                        }

                        if (action.equals("close"))
                        {
                            Systems.gui.displayCamera(false, uuid);
                            continue;
                        }
                    }

                    JSONObject status = (new IOTStatus(uuid)).toJson();
                    JSONObject metadata = (new IOTMetadata(uuid)).toJson();
                    JSONObject credentials = (new IOTCredential(uuid)).toJson();

                    JSONObject todo = new JSONObject();

                    Json.put(todo, "action", doaction);
                    Json.put(todo, "device", device.toJson());
                    Json.put(todo, "status", status);
                    Json.put(todo, "metadata", metadata);
                    Json.put(todo, "credentials", credentials);

                    if (device.driver.equals("p2p"))
                    {
                        Systems.p2p.doSomething(doaction, device.toJson(), status, credentials);
                        continue;
                    }

                    if (device.driver.equals("tpl"))
                    {
                        Systems.tpl.doSomething(doaction, device.toJson(), status, credentials);
                        continue;
                    }

                    if (device.driver.equals("sny"))
                    {
                        Systems.sny.doSomething(doaction, device.toJson(), status, credentials);
                        continue;
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
