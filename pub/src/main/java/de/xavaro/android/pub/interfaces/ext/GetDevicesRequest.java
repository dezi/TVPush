package de.xavaro.android.pub.interfaces.ext;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

public interface GetDevicesRequest
{
    @Nullable
    JSONObject onGetDeviceRequest(String uuid);

    @Nullable
    JSONObject onGetStatusRequest(String uuid);

    @Nullable
    JSONObject onGetCredentialRequest(String uuid);

    @Nullable
    JSONObject onGetMetaRequest(String uuid);

    @Nullable
    JSONArray onGetDevicesCapabilityRequest(String capability);
}
