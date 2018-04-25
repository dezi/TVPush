package de.xavaro.android.systems;

import android.app.Application;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.awx.base.AWX;
import de.xavaro.android.gui.base.GUI;

import de.xavaro.android.iam.base.IAM;
import de.xavaro.android.iam.eval.IAMEvalChannels;

import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTMetadata;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;

import de.xavaro.android.sny.base.SNY;
import de.xavaro.android.tpl.base.TPL;
import zz.top.p2p.base.P2P;

public class SystemsIAM extends IAM
{
    private static final String LOGTAG = SystemsIAM.class.getSimpleName();

    public SystemsIAM(Application application)
    {
        super(application);
    }

    @Override
    public void startSubsystem(String subsystem)
    {
        super.startSubsystem(subsystem);

        JSONObject channelsForDevices = new JSONObject();

        JSONArray tvremotes = SystemsIOT.instance.getDevicesWithCapability("tvremote");

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

        Log.d(LOGTAG, "startSubsystem: TV-Channels exported.");
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
                        if (GUI.instance != null) GUI.instance.displaySpeechRecognition(true);
                        continue;
                    }

                    if (action.equals("close"))
                    {
                        if (GUI.instance != null) GUI.instance.displaySpeechRecognition(false);
                        continue;
                    }
                }

                if (object.equals("streetview"))
                {
                    if (action.equals("open"))
                    {
                        String address = Json.getString(jaction, "objectWords");
                        if (GUI.instance != null) GUI.instance.displayStreetView(true, address);
                        continue;
                    }

                    if (action.equals("close"))
                    {
                        if (GUI.instance != null) GUI.instance.displayStreetView(false, null);
                        continue;
                    }
                }

                if (object.equals("wizzard"))
                {
                    if (action.equals("open"))
                    {
                        String wizzard = Json.getString(jaction, "objectWords");
                        if (GUI.instance != null) GUI.instance.displayWizzard(true, wizzard);
                        continue;
                    }

                    if (action.equals("close"))
                    {
                        if (GUI.instance != null) GUI.instance.displayWizzard(false, null);
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

                    IOTDevice device = IOTDevice.list.getEntry(uuid);
                    if ((device == null) || (device.type == null)) continue;

                    JSONObject doaction = Json.clone(jaction);
                    Json.remove(doaction, "devices");
                    Json.put(doaction, "uuid", uuid);

                    if (device.type.equals("camera"))
                    {
                        if (action.equals("open"))
                        {
                            if (GUI.instance != null) GUI.instance.displayCamera(true, uuid);
                            continue;
                        }

                        if (action.equals("close"))
                        {
                            if (GUI.instance != null) GUI.instance.displayCamera(false, uuid);
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

                    if (device.driver.equals("p2p") && (P2P.instance != null))
                    {
                        P2P.instance.doSomething(doaction, device.toJson(), status, credentials);
                        continue;
                    }

                    if (device.driver.equals("awx") && (AWX.instance != null))
                    {
                        AWX.instance.doSomething(doaction, device.toJson(), status, credentials);
                        continue;
                    }

                    if (device.driver.equals("tpl") && (TPL.instance != null))
                    {
                        TPL.instance.doSomething(doaction, device.toJson(), status, credentials);
                        continue;
                    }

                    if (device.driver.equals("sny") && (SNY.instance != null))
                    {
                        SNY.instance.doSomething(doaction, device.toJson(), status, credentials);
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
