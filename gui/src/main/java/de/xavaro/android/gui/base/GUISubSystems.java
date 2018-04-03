package de.xavaro.android.gui.base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.simple.Simple;
import pub.android.interfaces.all.SubSystemHandler;

public class GUISubSystems
{
    private final static String LOGTAG = GUISetup.class.getSimpleName();

    public final static int SUBSYSTEM_STATE_DEACTIVATED = 0;
    public final static int SUBSYSTEM_STATE_ACTIVATED = 1;
    public final static int SUBSYSTEM_STATE_DISABLED = 2;

    private final Map<String, JSONObject> subSystemsInfos = new HashMap<>();
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

        return (val != null) ? val : 0;
    }

    public int getSubsystemState(String drv)
    {
        JSONObject subsystem = Simple.getMapJSONObject(subSystemsInfos, drv);

        if (subsystem != null)
        {
            int mode = Json.getInt(subsystem, "mode");

            if (mode == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
            {
                return SUBSYSTEM_STATE_ACTIVATED;
            }
        }

        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);

        return Json.getInt(pref, "state");
    }

    public void setSubsystemState(String drv, int state)
    {
        JSONObject subsystem = Simple.getMapJSONObject(subSystemsInfos, drv);

        if (subsystem != null)
        {
            int mode = Json.getInt(subsystem, "mode");

            if (mode == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
            {
                return;
            }
        }

        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);
        Json.put(pref, "state", state);
        GUIPrefs.savePref(key, pref);
    }

    public boolean isSubsystemActivated(String drv)
    {
        JSONObject subsystem = Simple.getMapJSONObject(subSystemsInfos, drv);

        if (subsystem != null)
        {
            int mode = Json.getInt(subsystem, "mode");

            if (mode == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
            {
                return true;
            }
        }

        String key = "subsystem." + drv;

        JSONObject pref = GUIPrefs.readPref(key);

        return (Json.getInt(pref, "state") == SUBSYSTEM_STATE_ACTIVATED);
    }
}
