package zz.top.sny.base;

import android.support.annotation.Nullable;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import zz.top.utl.Json;

public class SNYUtil
{
    private final static String LOGTAG = SNYUtil.class.getSimpleName();

    @Nullable
    public static String getPostXML(String urlstr, String post, String authcookie)
    {
        return getPostInternal(urlstr, post, null, null, authcookie);
    }

    @Nullable
    public static JSONObject getPost(String urlstr, JSONObject post)
    {
        return getPostInternal(urlstr, post, null, null, null);
    }

    @Nullable
    public static JSONObject getPostAuth(String urlstr, JSONObject post, String user, String pass)
    {
        return getPostInternal(urlstr, post, user, pass, null);
    }

    @Nullable
    private static JSONObject getPostInternal(String urlstr, JSONObject post, String user, String pass, String authcookie)
    {
        String result = getPostInternal(urlstr, post.toString(), user, pass, authcookie);

        return Json.fromStringObject(result);
    }

    @Nullable
    private static String getPostInternal(String urlstr, String post, String user, String pass, String authcookie)
    {
        try
        {
            URL url = new URL(urlstr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            if ((user != null) && (pass != null) && ! pass.isEmpty())
            {
                String uspw = user + ":" + pass;
                String auth = new String(Base64.encode(uspw.getBytes(), Base64.NO_WRAP));

                connection.setRequestProperty("Authorization", "basic " + auth);
            }

            if ((authcookie != null) && ! authcookie.isEmpty())
            {
                //
                // Content-Type: text/xml,
                // SoapAction: "urn:schemas-sony-com:service:IRCC:1#X_SendIRCC",
                // Cookie: auth=
                //

                connection.setRequestProperty("Content-Type", "text/xml");
                connection.setRequestProperty("SoapAction", "\"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\"");
                connection.setRequestProperty("Cookie", "auth=" + authcookie);
            }

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setConnectTimeout(4000);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.connect();

            OutputStream os = connection.getOutputStream();
            os.write(post.getBytes("UTF-8"));
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

            return result;
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }

}
