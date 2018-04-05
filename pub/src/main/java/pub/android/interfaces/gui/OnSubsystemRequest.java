package pub.android.interfaces.gui;

import android.support.annotation.Nullable;

import org.json.JSONObject;

public interface OnSubsystemRequest
{
    JSONObject onGetSubsystemSettings(String subsystem);

    void onStartSubsystemRequest(String subsystem);

    void onStopSubsystemRequest(String subsystem);
}
