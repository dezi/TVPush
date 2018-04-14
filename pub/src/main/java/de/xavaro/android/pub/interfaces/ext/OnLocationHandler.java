package de.xavaro.android.pub.interfaces.ext;

import org.json.JSONObject;

public interface OnLocationHandler
{
    void onLocationMeasurement(JSONObject measurement);
}
