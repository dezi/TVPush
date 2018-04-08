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
import java.util.Random;

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

    /*
    private String digestAuth()
    {
        String digestAuthStr = null;

        String uri = getURL().getPath();
        String nonce = authFields.get("nonce");
        String realm = authFields.get("realm");
        String qop = authFields.get("qop");
        String algorithm = authFields.get("algorithm");
        String cnonce = generateCNonce();
        String nc = "1";
        String ha1 = toMD5DigestString(concatWithSeparator(":", username, realm, password));
        String ha2 = toMD5DigestString(concatWithSeparator(":", requestMethod, uri));
        String response = null;
        if (!TextUtils.isEmpty(ha1) && !TextUtils.isEmpty(ha2))
            response = toMD5DigestString(concatWithSeparator(":", ha1, nonce, nc, cnonce, qop, ha2));

        if (response != null) {
            StringBuilder sb = new StringBuilder(128);
            sb.append("Digest ");
            sb.append("username").append("=\"").append(username).append("\", ");
            sb.append("realm").append("=\"").append(realm).append("\", ");
            sb.append("nonce").append("=\"").append(nonce).append("\", ");
            sb.append("uri").append("=\"").append(uri).append("\", ");
            sb.append("qop").append("=\"").append(qop).append("\", ");
            sb.append("nc").append("=\"").append(nc).append("\", ");
            sb.append("cnonce").append("=\"").append(cnonce).append("\"");
            sb.append("response").append("=\"").append(response).append("\"");
            sb.append("algorithm").append("=\"").append(algorithm).append("\"");
            digestAuthStr = sb.toString();
        }
    }
    */

    private static String generateCNonce()
    {
        String s = "";
        for (int i = 0; i < 8; i++)
            s += Integer.toHexString(new Random().nextInt(16));
        return s;
    }
}
