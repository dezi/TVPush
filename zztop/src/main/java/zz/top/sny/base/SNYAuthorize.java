package zz.top.sny.base;

import android.os.StrictMode;
import android.support.annotation.Nullable;
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

    public static void authorize(String ipaddr, String uuid, String devname, String username)
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
        Json.put(client, "clientid", username + ":" + uuid);
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

        JSONObject result = getPost(urlstring, register);

        Log.d(LOGTAG, "authorize result=" + Json.toPretty(result));
    }

    @Nullable
    public static JSONObject getPost(String urlstr, JSONObject post)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

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
