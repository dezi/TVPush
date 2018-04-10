package de.xavaro.android.edx.comm;

import android.os.Build;
import android.support.annotation.Nullable;

import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import de.xavaro.android.edx.simple.Log;

public class EDXPostCloud
{
    private static final String LOGTAG = EDXPostCloud.class.getSimpleName();

    private static final String APP_ID = "R2SBa8tVBeMHzKRY8yIlle2dEMBNPQbRfsrImkcz";
    private static final String CLIENT_KEY = "vydx1cfuzIkzGYbuHolAh5MzmVMPKkLEXBdbQsbA";
    private static final String USER_AGENT = "Parse Android SDK 1.15.8 (com.edimax.edismart/4) API Level 25";

    @Nullable
    public static String getPost(String urlstr, String post, String sessionToken)
    {
        try
        {
            URL url = new URL(urlstr);

            Log.d(LOGTAG, "getPost: urlstr=" + urlstr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(4000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestProperty("User-Agent", USER_AGENT);

            connection.setRequestProperty("X-Parse-Application-Id", APP_ID);
            connection.setRequestProperty("X-Parse-Client-Key", CLIENT_KEY);
            connection.setRequestProperty("X-Parse-App-Build-Version", "4");
            connection.setRequestProperty("X-Parse-App-Display-Version", "1.0.1");
            connection.setRequestProperty("X-Parse-OS-Version", Build.VERSION.RELEASE);

            if (sessionToken != null)
            {
                connection.setRequestProperty("X-Parse-Session-Token", sessionToken);
            }

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "" + post.getBytes().length);

            //
            // Dump request headers.
            //

            /*
            for (Map.Entry<String, List<String>> entries : connection.getRequestProperties().entrySet())
            {
                String key = entries.getKey();

                for (String val : entries.getValue())
                {
                    Log.d(LOGTAG, "getPost: request key=" + key + " val=" + val);
                }
            }
            */

            connection.connect();

            Log.d(LOGTAG, "getPost: post=" + post);

            OutputStream os = connection.getOutputStream();
            os.write(post.getBytes());
            os.flush();
            os.close();

            //
            // Dump repsonse headers.
            //

            /*
            for (Map.Entry<String, List<String>> entries : connection.getHeaderFields().entrySet())
            {
                String key = entries.getKey();
                if (key == null) key = "Status-Line";

                for (String val : entries.getValue())
                {
                    Log.d(LOGTAG, "getPost: response key=" + key + " val=" + val);
                }
            }
            */

            if ((connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    && (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED))
            {
                //
                // File cannot be loaded.
                //

                Log.e(LOGTAG, "getPost: ERR=" + connection.getResponseCode());

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
                buffer = new byte[length];

                for (int xfer; total < length; total += xfer)
                {
                    xfer = input.read(buffer, total, length - total);
                }
            } else
            {
                byte[] chunk = new byte[32 * 1024];

                buffer = new byte[0];

                for (int xfer; ; total += xfer)
                {
                    xfer = input.read(chunk, 0, chunk.length);
                    if (xfer <= 0) break;

                    byte[] temp = new byte[buffer.length + xfer];
                    System.arraycopy(buffer, 0, temp, 0, buffer.length);
                    System.arraycopy(chunk, 0, temp, buffer.length, xfer);
                    buffer = temp;
                }
            }

            input.close();

            return new String(buffer);
        }
        catch (Exception ex)
        {
            Log.d(LOGTAG, ex.toString());
        }

        return null;
    }
}
