package de.xavaro.android.edx.comm;

import android.support.annotation.Nullable;

import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.net.HttpURLConnection;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Random;
import java.util.List;
import java.util.Map;
import java.net.URL;

import de.xavaro.android.edx.simple.Json;

public class EDXPostDevice
{
    private static final String LOGTAG = EDXPostDevice.class.getSimpleName();

    @Nullable
    public static String getPost(String urlstr, String post, JSONObject headers, String user, String pass)
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

            connection.setRequestProperty("Content-Type", "application/xml");
            connection.setRequestProperty("Content-Length", "" + post.getBytes().length);

            basicAuth(connection, user, pass);

            //
            // Dump request headers.
            //

            for (Map.Entry<String, List<String>> entries : connection.getRequestProperties().entrySet())
            {
                String key = entries.getKey();
                if (key == null) key = "Status-Line";

                for (String val : entries.getValue())
                {
                    Log.d(LOGTAG, "getPost: request key=" + key + " val=" + val);

                    if (headers != null) Json.put(headers, key, val);
                }
            }

            connection.connect();

            Log.d(LOGTAG, "getPost: post=" + post);

            OutputStream os = connection.getOutputStream();
            os.write(post.getBytes());
            os.flush();
            os.close();

            //
            // Dump repsonse headers.
            //

            for (Map.Entry<String, List<String>> entries : connection.getHeaderFields().entrySet())
            {
                String key = entries.getKey();
                if (key == null) key = "Status-Line";

                for (String val : entries.getValue())
                {
                    Log.d(LOGTAG, "getPost: response key=" + key + " val=" + val);

                    if (headers != null) Json.put(headers, key, val);
                }
            }

            if (connection.getResponseCode() == HttpURLConnection.HTTP_UNAUTHORIZED)
            {
                //
                // Retry with digest authorization.
                //

                String authheader = connection.getHeaderField("WWW-Authenticate");

                if (authheader != null)
                {
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Connection", "close");
                    connection.setConnectTimeout(4000);
                    connection.setUseCaches(false);
                    connection.setDoOutput(true);
                    connection.setDoInput(true);

                    digestAuth(connection, authheader, user, pass);

                    connection.connect();

                    os = connection.getOutputStream();
                    os.write(post.getBytes());
                    os.flush();
                    os.close();
                }
            }

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

    private static void basicAuth(HttpURLConnection connection, String user, String pass)
    {
        if ((user == null) || (pass == null)) return;

        String uspw = user + ":" + pass;
        String auth = new String(Base64.encode(uspw.getBytes(), Base64.DEFAULT));

        connection.setRequestProperty("Authorization", "Basic " + auth);

        Log.d(LOGTAG, "basicAuth: user=" + user + " pass=" + pass);
        Log.d(LOGTAG, "basicAuth: Basic " + auth);
    }

    private static void digestAuth(HttpURLConnection connection, String authrequest, String user, String pass)
    {
        if ((user == null) || (pass == null)) return;

        Map<String, String> authFields = parseAuthHeader(authrequest);

        String uri = connection.getURL().getPath();
        String method = connection.getRequestMethod();
        String nonce = authFields.get("nonce");
        String realm = authFields.get("realm");
        String qop = authFields.get("qop");
        String cnonce = generateNonce();
        String nc = "00000001";

        String ha1 = md5Hex(user + ":" + realm + ":" + pass);
        String ha2 = md5Hex(method + ":" + uri);

        String response = md5Hex(ha1 + ":" + nonce + ":" + nc + ":" + cnonce + ":" + qop + ":" + ha2);

        StringBuilder sb = new StringBuilder();

        sb.append("username").append("=\"").append(user).append("\", ");
        sb.append("realm").append("=\"").append(realm).append("\", ");
        sb.append("nonce").append("=\"").append(nonce).append("\", ");
        sb.append("uri").append("=\"").append(uri).append("\", ");
        sb.append("qop").append("=\"").append(qop).append("\", ");
        sb.append("nc").append("=\"").append(nc).append("\", ");
        sb.append("cnonce").append("=\"").append(cnonce).append("\", ");
        sb.append("response").append("=\"").append(response).append("\"");

        String auth = sb.toString();

        connection.setRequestProperty("Authorization", "Digest " + auth);

        Log.d(LOGTAG, "digestAuth: user=" + user + " pass=" + pass);
        Log.d(LOGTAG, "digestAuth: Digest " + auth);
    }

    private static Map<String, String> parseAuthHeader(String headerString)
    {
        String headerStringWithoutScheme = headerString.substring(headerString.indexOf(" ") + 1).trim();

        Map<String, String> values = new HashMap<>();

        String keys[] = headerStringWithoutScheme.split(",");

        for (String keyval : keys)
        {
            if (keyval.contains("="))
            {
                String key = keyval.substring(0, keyval.indexOf("=")).trim();
                String val = keyval.substring(keyval.indexOf("=") + 1);
                values.put(key.trim(), val.replaceAll("\"", "").trim());
            }
        }

        return values;
    }

    private static String md5Hex(String str)
    {
        try
        {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(str.getBytes());
            byte digestBytes[] = digest.digest();

            StringBuilder hexString = new StringBuilder();

            for (byte byt : digestBytes)
            {
                String hexByte = Integer.toHexString(byt & 0xff);
                if (hexByte.length() < 2) hexByte = "0" + hexByte;
                hexString.append(hexByte);
            }

            return hexString.toString();
        }
        catch (Exception ignore)
        {
        }

        return "";
    }

    private static String generateNonce()
    {
        String nonce = "";

        for (int inx = 0; inx < 16; inx++)
        {
            nonce += Integer.toHexString(new Random().nextInt(16));
        }

        return nonce;
    }
}
