package de.xavaro.android.gui.base;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class GUISubSystems
{
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
}
