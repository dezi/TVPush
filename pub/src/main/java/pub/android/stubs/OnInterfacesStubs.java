package pub.android.stubs;

import android.util.Log;

import org.json.JSONObject;

public class OnInterfacesStubs
{
    private String LOGTAG()
    {
        return this.getClass().getSimpleName();
    }

    public void onSubsystemStarted(String subsystem, int state)
    {
        Log.d(LOGTAG(), "onSubsystemStarted: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    public void onSubsystemStopped(String subsystem, int state)
    {
        Log.d(LOGTAG(), "onSubsystemStopped: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

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

    public void onPincodeRequest(String uuid)
    {
        Log.d(LOGTAG(), "onPincodeRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }

    public void onBackgroundRequest()
    {
        Log.d(LOGTAG(), "onBackgroundRequest: STUB!");
        Log.d(LOGTAG(), Log.getStackTraceString(new Exception()));
    }
}