package pub.android.stubs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;

public class OnInterfacesStubs
{
    private String LOGTAG()
    {
        return this.getClass().getSimpleName();
    }

    //region SubSystemHandler

    public int getSubsystemState(String subsystem)
    {
        Log.d(LOGTAG(), "getSubsystemState: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));

        return SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED;
    }

    public void setSubsystemState(String subsystem, int state)
    {
        Log.d(LOGTAG(), "setSubsystemState: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    public void onSubsystemStarted(String subsystem, int runstate)
    {
        Log.d(LOGTAG(), "onSubsystemStarted: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    public void onSubsystemStopped(String subsystem, int runstate)
    {
        Log.d(LOGTAG(), "onSubsystemStopped: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    //endregion SubSystemHandler

    //region OnDeviceHandler

    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG(), "onDeviceFound: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    public void onDeviceStatus(JSONObject device)
    {
        Log.d(LOGTAG(), "onDeviceStatus: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    public void onDeviceMetadata(JSONObject metadata)
    {
        Log.d(LOGTAG(), "onDeviceMetadata: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    public void onDeviceCredentials(JSONObject credentials)
    {
        Log.d(LOGTAG(), "onDeviceCredentials: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    //endregion OnDeviceHandler

    //region OnPincodeRequest

    public void onPincodeRequest(String uuid)
    {
        Log.d(LOGTAG(), "onPincodeRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    //endregion OnPincodeRequest

    //region OnBackgroundRequest

    public void onBackgroundRequest()
    {
        Log.d(LOGTAG(), "onBackgroundRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    //endregion OnBackgroundRequest

    //region GetDevicesRequest

    public JSONObject onGetDeviceRequest(String uuid)
    {
        Log.d(LOGTAG(), "onGetDeviceRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));

        return null;
    }

    public JSONObject onGetStatusRequest(String uuid)
    {
        Log.d(LOGTAG(), "onGetStatusRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));

        return null;
    }

    public JSONObject onGetCredentialRequest(String uuid)
    {
        Log.d(LOGTAG(), "onGetCredentialRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));

        return null;
    }

    public JSONObject onGetMetaRequest(String uuid)
    {
        Log.d(LOGTAG(), "onGetMetaRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));

        return null;
    }

    public JSONArray onGetDevicesCapabilityRequest(String capability)
    {
        Log.d(LOGTAG(), "onGetDevicesCapabilityRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));

        return null;
    }

    //endregion GetDevicesRequest
}
