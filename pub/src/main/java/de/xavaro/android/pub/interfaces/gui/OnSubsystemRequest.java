package de.xavaro.android.pub.interfaces.gui;

import org.json.JSONObject;

public interface OnSubsystemRequest
{
    JSONObject onGetSubsystemSettings(String subsystem);

    void onStartSubsystemRequest(String subsystem);

    void onStopSubsystemRequest(String subsystem);
}
