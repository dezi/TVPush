package de.xavaro.android.gui.base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;

public class GUISubSystems
{
    public final static int SUBSYSTEM_STATE_DEACTIVATED = 0;
    public final static int SUBSYSTEM_STATE_ACTIVATED = 1;
    public final static int SUBSYSTEM_STATE_DISABLED = 2;

    private final ArrayList<JSONObject> subSystemsInfos = new ArrayList<>();

    private final Map<String, Integer> subSystemsRunstates = new HashMap<>();

    public void registerSubsystem(JSONObject driverInfo)
    {
        synchronized (subSystemsInfos)
        {
            if (!subSystemsInfos.contains(driverInfo))
            {
                subSystemsInfos.add(driverInfo);
            }
        }
    }

    public void registerSubsystemRunstate(String subsystem, int state)
    {
        synchronized (subSystemsRunstates)
        {
            subSystemsRunstates.put(subsystem, state);
        }
    }

    public JSONArray getRegisteredSubsystems()
    {
        JSONArray subsys = new JSONArray();

        for (JSONObject subSystem : subSystemsInfos)
        {
            subsys.put(subSystem);
        }

        return subsys;
    }

    public int getSubsystemsRunState(String subsystem)
    {
        Integer val = Simple.getMapInteger(subSystemsRunstates, subsystem);

        return (val != null) ? val : 0;
    }

    public int getSubsystemState(String drv)
    {
        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);

        return Json.getInt(pref, "state");
    }

    public void setSubsystemState(String drv, int state)
    {
        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);
        Json.put(pref, "state", state);
        GUIPrefs.savePref(key, pref);
    }

    public boolean isSubsystemActivated(String drv)
    {
        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);

        return (Json.getInt(pref, "state") == SUBSYSTEM_STATE_ACTIVATED);
    }
}
