package zz.top.sny.base;

import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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
    // 46A461229C74DFB1B168C5AC6A9DA77EE8D481E4
    //

    //
    // UUID = 5b12df94-9e63-77bf-7c8c-d66a430994fb
    // COOKIE = 18DF5D5C3B06220A1D6186896BC1462CB2F74616
    //

    public static void authorize(String ipaddr, String snytvuuid, String devname, String username)
    {
        requestAuth(ipaddr, snytvuuid, devname, username);

        registerPincode(ipaddr, snytvuuid, devname, username, "1234");
    }

    public static void requestAuth(String ipaddr, String snytvuuid, String devname, String username)
    {
        authHandler(ipaddr, snytvuuid, devname, username, null);
    }

    public static void registerPincode(String ipaddr, String snytvuuid, String devname, String username, String pincode)
    {
        authHandler(ipaddr, snytvuuid, devname, null, pincode);
    }

    public static void authHandler(String ipaddr, String snytvuuid, String devname, String username, String password)
    {
        JSONObject register = new JSONObject();

        Json.put(register, "method", "actRegister");
        Json.put(register, "id", 8);
        Json.put(register, "version", "1.0");

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

        Log.d(LOGTAG, "authorize result=" + Json.toPretty(register));

        JSONObject result;

        if ((password == null) || password.isEmpty())
        {
            result = SNYUtil.getPost(urlstring, register);

        }
        else
        {
            result = SNYUtil.getPostAuth(urlstring, register, "", password);
        }

        Log.d(LOGTAG, "authorize result=" + Json.toPretty(result));
    }
}
