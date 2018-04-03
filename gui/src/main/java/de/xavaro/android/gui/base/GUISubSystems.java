package de.xavaro.android.gui.base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.HashMap;
import java.util.Map;

import pub.android.interfaces.all.SubSystemHandler;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;

public class GUISubSystems
{
    private final static String LOGTAG = GUISetup.class.getSimpleName();

    private final Map<String, JSONObject> subSystemsInfos = new LinkedHashMap<>();
    private final Map<String, Integer> subSystemsRunstates = new HashMap<>();

    public void registerSubsystem(JSONObject driverInfo)
    {
        String driver = Json.getString(driverInfo, "drv");
        if (driver == null) return;

        synchronized (subSystemsInfos)
        {
            subSystemsInfos.put(driver, driverInfo);
        }
    }

    public void registerSubsystemRunstate(String subsystem, int runstate)
    {
        synchronized (subSystemsRunstates)
        {
            subSystemsRunstates.put(subsystem, runstate);
        }
    }

    public JSONArray getRegisteredSubsystems()
    {
        JSONArray subsys = new JSONArray();

        for (Map.Entry<String, JSONObject> entry : subSystemsInfos.entrySet())
        {
            subsys.put(entry.getValue());
        }

        return subsys;
    }

    public int getSubsystemsRunState(String subsystem)
    {
        Integer val = Simple.getMapInteger(subSystemsRunstates, subsystem);
        return (val != null) ? val : SubSystemHandler.SUBSYSTEM_RUN_STOPPED;
    }

    public int getSubsystemsMode(String subsystem)
    {
        JSONObject subsystemInfo = Simple.getMapJSONObject(subSystemsInfos, subsystem);

        if (subsystemInfo != null)
        {
            return Json.getInt(subsystemInfo, "mode");
        }

        return SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY;
    }

    public int getSubsystemState(String subsystem)
    {
        if (getSubsystemsMode(subsystem) == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
        {
            return SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED;
        }

        String key = "subsystem." + subsystem;

        JSONObject pref = GUIPrefs.readPref(key);

        return Json.getInt(pref, "state");
    }

    public void setSubsystemState(String subsystem, int state)
    {
        if (getSubsystemsMode(subsystem) == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
        {
            return;
        }

        String key = "subsystem." + subsystem;

        JSONObject pref = GUIPrefs.readPref(key);
        Json.put(pref, "state", state);
        GUIPrefs.savePref(key, pref);
    }

    public boolean isSubsystemActivated(String subsystem)
    {
        if (getSubsystemsMode(subsystem) == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
        {
            return true;
        }

        String key = "subsystem." + subsystem;

        JSONObject pref = GUIPrefs.readPref(key);

        return (Json.getInt(pref, "state") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);
    }
}
