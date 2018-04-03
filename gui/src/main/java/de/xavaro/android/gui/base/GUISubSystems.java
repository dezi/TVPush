package de.xavaro.android.gui.base;

import android.support.annotation.Nullable;

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

    @Nullable
    public JSONObject getSubsystemInfos(String subsystem)
    {
        JSONObject subsystemInfo = Simple.getMapJSONObject(subSystemsInfos, subsystem);

        if (subsystemInfo != null)
        {
            String[] parts = subsystem.split("\\.");

            if (parts.length == 2)
            {
                JSONArray settings = Json.getArray(subsystemInfo, "settings");

                if (settings != null)
                {
                    for (int inx = 0; inx < settings.length(); inx++)
                    {
                        JSONObject setting = Json.getObject(settings, inx);
                        if (setting == null) continue;

                        String tag = Json.getString(setting, "tag");
                        if ((tag == null) || ! tag.equals(parts[ 1 ])) continue;

                        subsystemInfo = setting;
                    }
                }
            }
        }

        return subsystemInfo;
    }

    public int getSubsystemRunState(String subsystem)
    {
        Integer val = Simple.getMapInteger(subSystemsRunstates, subsystem);
        return (val != null) ? val : SubSystemHandler.SUBSYSTEM_RUN_STOPPED;
    }

    public int getSubsystemState(String subsystem)
    {
        if (getSubsystemMode(subsystem) == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
        {
            return SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED;
        }

        String key = "subsystem." + subsystem;
        JSONObject pref = GUIPrefs.readPref(key);

        return Json.getInt(pref, "state");
    }

    public void setSubsystemState(String subsystem, int state)
    {
        if (getSubsystemMode(subsystem) == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
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
        if (getSubsystemMode(subsystem) == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
        {
            return true;
        }

        String key = "subsystem." + subsystem;
        JSONObject pref = GUIPrefs.readPref(key);

        return (Json.getInt(pref, "state") == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);
    }

    private int getSubsystemMode(String subsystem)
    {
        JSONObject subsystemInfo = getSubsystemInfos(subsystem);

        if (subsystemInfo != null)
        {
            return Json.getInt(subsystemInfo, "mode");
        }

        return SubSystemHandler.SUBSYSTEM_MODE_VOLUNTARY;
    }
}
