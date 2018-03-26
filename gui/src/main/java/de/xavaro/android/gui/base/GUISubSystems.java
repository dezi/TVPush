package de.xavaro.android.gui.base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.xavaro.android.gui.simple.Json;

public class GUISubSystems
{
    public final static int SUBSYSTEM_STATE_INACTIVE = 0;
    public final static int SUBSYSTEM_STATE_ACTIVATED = 1;
    public final static int SUBSYSTEM_STATE_DISABLED = 2;

    private final ArrayList<JSONObject> subSystems = new ArrayList<>();

    public void registerSubsystem(JSONObject driverInfo)
    {
        if (! subSystems.contains(driverInfo))
        {
            subSystems.add(driverInfo);
        }
    }

    public JSONArray getRegisteredSubsystems()
    {
        JSONArray subsys = new JSONArray();

        for (JSONObject subSystem : subSystems)
        {
            subsys.put(subSystem);
        }

        return subsys;
    }

    public static int getSubsystemState(String drv)
    {
        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);

        return Json.getInt(pref, "state");
    }

    public static void setSubsystemState(String drv, int state)
    {
        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);
        Json.put(pref, "state", state);
        GUIPrefs.savePref(key, pref);
    }
}
