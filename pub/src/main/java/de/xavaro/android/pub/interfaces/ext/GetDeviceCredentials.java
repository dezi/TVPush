package de.xavaro.android.pub.interfaces.ext;

import android.support.annotation.Nullable;

import org.json.JSONObject;

public interface GetDeviceCredentials
{
    @Nullable
    JSONObject getDeviceCredentials(String uuid);
}
