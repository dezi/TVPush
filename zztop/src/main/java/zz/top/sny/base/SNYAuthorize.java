package zz.top.sny.base;

import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import zz.top.utl.Json;

public class SNYAuthorize
{
    private final static String LOGTAG = SNYAuthorize.class.getSimpleName();

    private final static String authurl = "http://#/sony/accessControl";

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
        registerPincode(ipaddr, snytvuuid, devname, username, null);
    }

    public static void registerPincode(String ipaddr, String snytvuuid, String devname, String username, String pincode)
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

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

        String urlstring = authurl.replace("#", ipaddr);

        Log.d(LOGTAG, "authorize result=" + Json.toPretty(register));

        JSONObject result = getPost(urlstring, register, "", pincode);

        Log.d(LOGTAG, "authorize result=" + Json.toPretty(result));
    }

    @Nullable
    public static JSONObject getPost(String urlstr, JSONObject post, String user, String pass)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if ((user != null) && (pass != null) && ! pass.isEmpty())
            {
                String auth = new String(Base64.encode("myuser:mypass".getBytes(), Base64.NO_WRAP));
                connection.setRequestProperty("Authorization", "basic " + auth);
            }

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(4000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            OutputStream os = connection.getOutputStream();
            os.write(post.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                //
                // File cannot be loaded.
                //

                Log.d(LOGTAG, "getPost: ERR=" + connection.getResponseCode());

                return null;
            }

            //
            // Fetch file.
            //

            int length = connection.getContentLength();
            InputStream input = connection.getInputStream();
            byte[] buffer;
            int total = 0;

            if (length > 0)
            {
                buffer = new byte[ length ];

                for (int xfer; total < length; total += xfer)
                {
                    xfer = input.read(buffer, total, length - total);
                }
            }
            else
            {
                byte[] chunk = new byte[ 32 * 1024 ];

                buffer = new byte[ 0 ];

                for (int xfer; ; total += xfer)
                {
                    xfer = input.read(chunk, 0, chunk.length);
                    if (xfer <= 0) break;

                    byte[] temp = new byte[ buffer.length + xfer ];
                    System.arraycopy(buffer, 0, temp, 0, buffer.length);
                    System.arraycopy(chunk, 0, temp, buffer.length, xfer);
                    buffer = temp;
                }
            }

            input.close();

            String result = new String(buffer);

            Log.d(LOGTAG, "getPost result=" + result);

            return Json.fromStringObject(result);
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }
}
