package de.xavaro.android.edx.comm;

import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import de.xavaro.android.edx.simple.Json;

public class EDXUtil
{
    private static final String LOGTAG = EDXUtil.class.getSimpleName();

    @Nullable
    public static String getPost(String urlstr, String post, JSONObject headers, String user, String pass)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            /*
            if ((user != null) && (pass != null) && ! pass.isEmpty())
            {
                String uspw = user + ":" + pass;
                String auth = new String(Base64.encode(uspw.getBytes(),Base64.DEFAULT));

                connection.setRequestProperty("Authorization", "basic " + auth);

                Log.d(LOGTAG, "getPost: user=" + user + " pass=" + pass + " auth=" + auth);
            }
            */

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Connection", "close");
            connection.setDoOutput(true);
            connection.setConnectTimeout(4000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            OutputStream os = connection.getOutputStream();
            os.write(post.getBytes());
            os.flush();
            os.close();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK)
            {
                //
                // File cannot be loaded.
                //

                Log.e(LOGTAG, "getPostInternal: ERR=" + connection.getResponseCode());

                return null;
            }

            //
            // Dump repsonse headers.
            //

            if (headers != null)
            {
                for (Map.Entry<String, List<String>> entries : connection.getHeaderFields().entrySet())
                {
                    String key = entries.getKey();
                    if (key == null) key = "Status-Line";

                    for (String val : entries.getValue())
                    {
                        Log.d(LOGTAG, "getPostInternal: response key=" + key + " val=" + val);

                        Json.put(headers, key, val);
                    }
                }
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

            Log.d(LOGTAG, "getPostInternal: result=" + result);

            return result;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }
}
