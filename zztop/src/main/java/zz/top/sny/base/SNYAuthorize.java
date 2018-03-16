package zz.top.sny.base;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import zz.top.utl.Json;

public class SNYAuthorize
{
    private final static String LOGTAG = SNYAuthorize.class.getSimpleName();

    private final static String authurl = "http://####/sony/accessControl";

    //
    //  {
    //    "method": "actRegister",
    //    "params":
    //      [
    //        {
    //          "clientid":"Dezi Blaubs:035FC338-B56D-4418-9163-42657A17CDFE",
    //          "nickname":"Dezi Blaubs (Dezi Bla)",
    //          "level":"private"
    //        },
    //        [
    //          {
    //            "value":"yes",
    //            "function":"WOL"
    //          }
    //        ]
    //      ],
    //    "id":8,
    //    "version":"1.0"
    // }
    //

    //
    // Set-Cookie: auth=B6AF8A02D04CE91A00CDE4D8C4448DD1E238D814; Path=/sony/; Max-Age=1209600; Expires=Di., 27 März 2018 18:38:51 GMT+00:00
    //
    // UUID = 5b12df94-9e63-77bf-7c8c-d66a430994fb
    // COOKIE = 18DF5D5C3B06220A1D6186896BC1462CB2F74616
    //

    private static String currentIPAddr;
    private static String currentSNYtvuuid;
    private static String currentDevname;
    private static String currentUsername;

    public static boolean enterPincode(String pincode)
    {
        boolean ok = false;

        if (currentIPAddr != null)
        {
            ok = registerPincode(currentIPAddr, currentSNYtvuuid, currentDevname, currentUsername, pincode);
        }

        if (ok)
        {
            currentIPAddr = null;
            currentSNYtvuuid = null;
            currentDevname = null;
            currentUsername = null;
        }

        return ok;
    }

    public static void requestAuth(String ipaddr, String snytvuuid, String devname, String username)
    {
        JSONObject credentials = authHandler(ipaddr, snytvuuid, devname, username, null);

        if (credentials != null)
        {
            SNY.instance.onDeviceCredentials(credentials);
        }
        else
        {
            currentIPAddr = ipaddr;
            currentSNYtvuuid = snytvuuid;
            currentDevname = devname;
            currentUsername = username;

            SNY.instance.onPincodeRequest(snytvuuid);
        }
    }

    private static boolean registerPincode(String ipaddr, String snytvuuid, String devname, String username, String pincode)
    {
        JSONObject credentials = authHandler(ipaddr, snytvuuid, devname, null, pincode);
        if (credentials != null) SNY.instance.onDeviceCredentials(credentials);

        return (credentials != null);
    }

    private static JSONObject authHandler(String ipaddr, String snytvuuid, String devname, String username, String password)
    {
        JSONObject register = new JSONObject();

        Json.put(register, "id", 8);
        Json.put(register, "version", "1.0");

        Json.put(register, "method", "actRegister");

        JSONArray params = new JSONArray();
        Json.put(register, "params", params);

        JSONObject client = new JSONObject();
        Json.put(client, "clientid", snytvuuid);
        Json.put(client, "nickname", username + " (" + devname + ")");
        Json.put(client, "level", "private");

        Json.put(params, client);

        JSONArray nochnarray = new JSONArray();
        Json.put(params, nochnarray);

        JSONObject nochnobject = new JSONObject();
        Json.put(nochnarray, nochnobject);

        Json.put(nochnobject, "value", "yes");
        Json.put(nochnobject, "function", "WOL");

        String urlstring = authurl.replace("####", ipaddr);

        Log.d(LOGTAG, "authorize urlstring=" + urlstring);

        //Log.d(LOGTAG, "authorize register=" + Json.toPretty(register));

        JSONObject result;
        JSONObject headers = new JSONObject();

        if ((password == null) || password.isEmpty())
        {
            result = SNYUtil.getPost(urlstring, register, headers);
        }
        else
        {
            result = SNYUtil.getPostAuth(urlstring, register, headers, "", password);
        }

        JSONObject credentials = new JSONObject();

        String cookie = Json.getString(headers, "Set-Cookie");

        if (cookie != null)
        {
            int pos1 = cookie.indexOf("auth=");

            if (pos1 >= 0)
            {
                pos1 += 5;

                int pos2 = cookie.substring(pos1).indexOf(";");

                if (pos2 > 0)
                {
                    String authtoken = cookie.substring(pos1).substring(0, pos2);

                    Log.d(LOGTAG, "authHandler: authtoken=" + authtoken);

                    Json.put(credentials, "uuid", snytvuuid);
                    Json.put(credentials, "clientid", snytvuuid);
                    Json.put(credentials, "authtoken", authtoken);
                    Json.put(credentials, "expires", System.currentTimeMillis() + 10 * 86400 * 1000);
                }
            }
        }

        Log.d(LOGTAG, "authorize result=" + Json.toPretty(result));

        return credentials;
    }
}
