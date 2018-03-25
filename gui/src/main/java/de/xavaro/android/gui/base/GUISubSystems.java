package de.xavaro.android.gui.base;

import org.json.JSONArray;

import java.util.ArrayList;

public class GUISubSystems
{
    private final ArrayList<String> subSystems = new ArrayList<>();

    public void registerSubsystem(String driverTag)
    {
        if (! subSystems.contains(driverTag))
        {
            subSystems.add(driverTag);
        }
    }

    public JSONArray getRegisteredSubsystems()
    {
        JSONArray subsys = new JSONArray();

        for (String subSystem : subSystems)
        {
            subsys.put(subSystem);
        }

        return subsys;
    }
}
