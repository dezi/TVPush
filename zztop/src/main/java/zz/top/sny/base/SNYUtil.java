package zz.top.sny.base;

import android.os.Build;
import android.preference.PreferenceActivity;
import android.support.annotation.Nullable;

import android.text.Html;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import zz.top.utl.Json;

public class SNYUtil
{
    private final static String LOGTAG = SNYUtil.class.getSimpleName();

    @Nullable
    public static String getPostXML(String urlstr, String post, String authcookie)
    {
        return getPostInternal(urlstr, post, null, null, null, authcookie);
    }

    @Nullable
    public static JSONObject getPost(String urlstr, JSONObject post)
    {
        return getPostInternal(urlstr, post, null, null, null, null);
    }

    @Nullable
    public static JSONObject getPost(String urlstr, JSONObject post, JSONObject headers)
    {
        return getPostInternal(urlstr, post, headers,null, null, null);
    }

    @Nullable
    public static JSONObject getPostAuth(String urlstr, JSONObject post, String user, String pass)
    {
        return getPostInternal(urlstr, post, null, user, pass, null);
    }

    @Nullable
    public static JSONObject getPostAuth(String urlstr, JSONObject post, JSONObject headers, String user, String pass)
    {
        return getPostInternal(urlstr, post, headers, user, pass, null);
    }

    @Nullable
    private static JSONObject getPostInternal(String urlstr, JSONObject post, JSONObject headers, String user, String pass, String authcookie)
    {
        String result = getPostInternal(urlstr, post.toString(), headers, user, pass, authcookie);

        return Json.fromStringObject(result);
    }

    @Nullable
    private static String getPostInternal(String urlstr, String post, JSONObject headers, String user, String pass, String authcookie)
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

                Log.e(LOGTAG, "getPost: ERR=" + connection.getResponseCode());

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

    @Nullable
    public static String HTMLdefuck(String htmlString)
    {
        //
        // Preface:
        //
        // I hate XML and all that bogus
        // fuck stuff coming with it.
        //
        // Long lives JSON!
        //

        if (htmlString == null)
        {
            //
            // Yah, fuck it.
            //

            return null;
        }

        //
        // Fuck dat one.
        //
        // Html.fromHtml fucks LF and CR.
        // So we fuck Html.fromHtml...
        //

        String split = null;

        if (htmlString.contains("\r\n"))
        {
            split = "\r\n";
        }
        else
        {
            if (htmlString.contains("\r"))
            {
                split = "\r";
            }
            else
            {
                if (htmlString.contains("\n"))
                {
                    split = "\n";
                }
            }
        }

        if (split == null)
        {
            if (Build.VERSION.SDK_INT >= 24)
            {
                return Html.fromHtml(htmlString, Html.FROM_HTML_MODE_LEGACY).toString();
            }
            else
            {
                return Html.fromHtml(htmlString).toString();
            }
        }

        String[] parts = htmlString.split(split);

        if (Build.VERSION.SDK_INT >= 24)
        {
            for (int inx = 0; inx < parts.length; inx++)
            {
                parts[inx] = Html.fromHtml(parts[inx], Html.FROM_HTML_MODE_LEGACY).toString();
            }
        }
        else
        {
            for (int inx = 0; inx < parts.length; inx++)
            {
                parts[inx] = Html.fromHtml(parts[inx]).toString();
            }
        }

        //
        // Fuck dat two.
        //
        // A simple basic String.join requires API >= 26.
        // How unbelievable fuckable that is!!
        //

        if (Build.VERSION.SDK_INT >= 26)
        {
            return String.join(split, parts);
        }

        StringBuilder build = new StringBuilder();

        for (int inx = 0; inx < parts.length; inx++)
        {
            if (inx > 0) build.append(split);

            build.append(parts[ inx ]);
        }

        return build.toString();
    }

    @Nullable
    public static String matchStuff(String text, String regex)
    {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group(1);

        return null;
    }
}
