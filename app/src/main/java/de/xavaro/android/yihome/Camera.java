package de.xavaro.android.yihome;

import android.util.Log;

import com.p2p.pppp_api.PPPP_Session;

import static com.p2p.pppp_api.PPPP_APIs.PPPP_Check;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_Close;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_ConnectOnlyLanSearch;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_DeInitialize;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_GetAPIVersion;
import static com.p2p.pppp_api.PPPP_APIs.PPPP_Initialize;

public class Camera
{
    private static final String LOGTAG = Camera.class.getSimpleName();

    static
    {
        System.loadLibrary("yihome-lib");
        System.loadLibrary("PPPP_API");
    }

    public static void initialize()
    {
        int resVersion = PPPP_GetAPIVersion();
        Log.d(LOGTAG, "initialize: PPPP_GetAPIVersion=" + resVersion);

        int resInit = PPPP_Initialize("".getBytes(), 12);
        Log.d(LOGTAG, "initialize: PPPP_Initialize=" + resInit);

        int resConnect = PPPP_ConnectOnlyLanSearch("TNPUSAC-663761-TLWPW");
        Log.d(LOGTAG, "initialize: PPPP_ConnectOnlyLanSearch=" + resConnect);

        PPPP_Session session = new PPPP_Session();

        int resCheck = PPPP_Check(resConnect, session);
        Log.d(LOGTAG, "initialize: PPPP_Check=" + resCheck);

        Log.d(LOGTAG, "initialize: getRemoteIP=" + session.getRemoteIP());
        Log.d(LOGTAG, "initialize: getRemotePort=" + session.getRemotePort());

        int resClose = PPPP_Close(resConnect);
        Log.d(LOGTAG, "initialize: PPPP_Close=" + resClose);

        int resDeinit = PPPP_DeInitialize();
        Log.d(LOGTAG, "initialize: PPPP_DeInitialize=" + resDeinit);
    }

    public static native String stringFromJNI();

    //public static native int PPPP_GetAPIVersion();
    //public static native int PPPP_ConnectOnlyLanSearch(String str);
}
