package de.xavaro.android.edx.comm;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import de.xavaro.android.edx.simple.Json;
import de.xavaro.android.edx.simple.Log;
import de.xavaro.android.edx.simple.Simple;

public class EDXCloud
{
    private static final String LOGTAG = EDXCloud.class.getSimpleName();

    private static final String cloudurl = "https://pg-app-c9c8cz82iexbxxdeebtxheb03v6hvs.scalabl.cloud/1";

    private static String sessionToken;
    private static String userObjectId;
    private static String installationId = UUID.randomUUID().toString();

    public static void updateSettings()
    {
        if (! getInstallation())
        {
            Log.e(LOGTAG, "updateSettings: getInstallation failed!");
            return;
        }

        if (! getSession())
        {
            Log.e(LOGTAG, "updateSettings: getSession failed!");
            return;
        }

        if (! getDevices())
        {
            Log.e(LOGTAG, "updateSettings: getDevices failed!");
        }
    }

    private static boolean getDevices()
    {
        JSONObject body = new JSONObject();

        //
        // Fuckdat happens shit stupid EDIMAX developers,
        // put string encoded JSON into JSON. Very
        // stupid.
        //
        // {"where":"{\"deviceOwner\":{\"__type\":\"Pointer\",\"className\":\"_User\",\"objectId\":\"o9sqXtoyQo\"}}","_method":"GET"}
        //

        JSONObject where = new JSONObject();
        JSONObject deviceOwner = new JSONObject();

        Json.put(where, "deviceOwner", deviceOwner);

        Json.put(deviceOwner, "__type", "Pointer");
        Json.put(deviceOwner, "className", "_User");
        Json.put(deviceOwner, "objectId", userObjectId);

        Json.put(body, "where", where.toString());
        Json.put(body, "_method", "GET");

        String url = cloudurl + "/classes/Device";

        String resultStr = getPost(url, body.toString(), null, null, null);
        Log.d(LOGTAG, "getDevices: resultStr=" + resultStr);

        JSONObject result = Json.fromStringObject(resultStr);
        JSONArray results = Json.getArray(result, "results");
        if (results == null) return false;

        for (int inx = 0; inx < results.length(); inx++)
        {
            JSONObject edidev = Json.getObject(results, inx);
            if (edidev == null) continue;

            buildDeviceDescription(edidev);
        }

        return true;
    }

    private static boolean getSession()
    {
        JSONObject body = new JSONObject();

        Json.put(body, "username", "dezi@kappa-mm.de");
        Json.put(body, "password", "hallo1234");
        Json.put(body, "_method", "GET");

        String url = cloudurl + "/login";

        String resultstr = getPost(url, body.toString(), null, null, null);
        Log.d(LOGTAG, "getSession: resultstr=" + resultstr);

        JSONObject result = Json.fromStringObject(resultstr);
        if (result == null) return false;

        userObjectId = Json.getString(result, "objectId");
        sessionToken = Json.getString(result, "sessionToken");

        Log.d(LOGTAG, "getSession: userObjectId=" + userObjectId + " sessionToken=" + sessionToken);

        return (userObjectId != null) && (sessionToken != null);
    }

    private static boolean getInstallation()
    {
        String body = ""
                + "{\"pushType\":\"gcm\","
                + "\"localeIdentifier\":\"de-DE\","
                + "\"appVersion\":\"1.0.1\","
                + "\"deviceType\":\"android\","
                + "\"appIdentifier\":\"com.edimax.edismart\","
                + "\"installationId\":\"" + installationId + "\","
                + "\"parseVersion\":\"1.15.8\","
                + "\"appName\":\"EdiSmart\","
                + "\"GCMSenderId\":\"id:869997638701\","
                + "\"timeZone\":\"Europe/Berlin\"}";

        String url = cloudurl + "/classes/_Installation";

        String resultStr = getPost(url, body, null, null, null);
        Log.d(LOGTAG, "getInstallation: result=" + resultStr);

        JSONObject result = Json.fromStringObject(resultStr);
        if (result == null) return false;

        String installationObjectId = Json.getString(result, "objectId");

        Log.d(LOGTAG, "getInstallation: installationObjectId=" + installationObjectId);

        return (installationObjectId != null);
    }

    private static void buildDeviceDescription(JSONObject edidev)
    {
        String name = Json.getString(edidev, "nickName");
        String version = Json.getString(edidev, "fwVer");
        String model = Json.getString(edidev, "modelNumber");
        String macaddr = Json.getString(edidev, "deviceMac");
        String localPass = Json.getString(edidev, "localPass");

        if ((name == null)
                || (model == null)
                || (version == null)
                || (macaddr == null)
                || (localPass == null))
        {
            Log.e(LOGTAG, "buildDevice failed edidev=" + Json.toPretty(edidev));
            return;
        }

        model = model.toUpperCase().replace("-", "");

        macaddr = macaddr.toLowerCase();

        macaddr = ""
                + macaddr.substring(0,2) + ":"
                + macaddr.substring(2, 4) + ":"
                + macaddr.substring(4, 6) + ":"
                + macaddr.substring(6, 8) + ":"
                + macaddr.substring(8, 10) + ":"
                + macaddr.substring(10, 12)
                ;

        String uuid = Simple.hmacSha1UUID(model, macaddr);
        String caps = EDXUtil.getCapabilities(model);

        JSONObject edimax = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(edimax, "device", device);

        Json.put(device, "uuid", uuid);
        Json.put(device, "name", name);
        Json.put(device, "nick", name);
        Json.put(device, "model", model);
        Json.put(device, "type", "smartplug");
        Json.put(device, "driver", "edx");
        Json.put(device, "brand", "edimax");
        Json.put(device, "macaddr", macaddr);
        Json.put(device, "version", version);

        Json.put(device, "capabilities", caps);

        //EDX.instance.onDeviceFound(edimax);

        android.util.Log.d(LOGTAG, "buildDeviceDescription: device=" + Json.toPretty(edimax));

        JSONObject credential = new JSONObject();
        JSONObject credentials = new JSONObject();

        Json.put(credential, "credentials", credentials);
        Json.put(credentials, "localPass", localPass);

        android.util.Log.d(LOGTAG, "buildDeviceDescription: credential=" + Json.toPretty(credential));
    }

    @Nullable
    public static String getPost(String urlstr, String post, JSONObject headers, String user, String pass)
    {
        try
        {
            URL url = new URL(urlstr);

            android.util.Log.d(LOGTAG, "getPost: urlstr=" + urlstr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setConnectTimeout(4000);
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setRequestProperty("User-Agent", "Parse Android SDK 1.15.8 (com.edimax.edismart/4) API Level 25");

            connection.setRequestProperty("X-Parse-Application-Id", "R2SBa8tVBeMHzKRY8yIlle2dEMBNPQbRfsrImkcz");
            connection.setRequestProperty("X-Parse-Client-Key", "vydx1cfuzIkzGYbuHolAh5MzmVMPKkLEXBdbQsbA");
            connection.setRequestProperty("X-Parse-Client-Version", "a1.15.8");
            connection.setRequestProperty("X-Parse-App-Build-Version", "4");
            connection.setRequestProperty("X-Parse-App-Display-Version", "1.0.1");
            connection.setRequestProperty("X-Parse-OS-Version", "7.1.1");

            //https://pg-app-c9c8cz82iexbxxdeebtxheb03v6hvs.scalabl.cloud/1/classes/Device
            if (sessionToken != null)
            {
                connection.setRequestProperty("X-Parse-Session-Token", sessionToken);
            }
            else
            {
                if (installationId != null)
                {
                    connection.setRequestProperty("X-Parse-Installation-Id", installationId);
                }
            }

            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Length", "" + post.getBytes().length);

            //
            // Dump request headers.
            //

            for (Map.Entry<String, List<String>> entries : connection.getRequestProperties().entrySet())
            {
                String key = entries.getKey();
                if (key == null) key = "Status-Line";

                for (String val : entries.getValue())
                {
                    android.util.Log.d(LOGTAG, "getPost: request key=" + key + " val=" + val);

                    if (headers != null) Json.put(headers, key, val);
                }
            }

            connection.connect();

            android.util.Log.d(LOGTAG, "getPost: post=" + post);

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
                    android.util.Log.d(LOGTAG, "getPost: response key=" + key + " val=" + val);

                    if (headers != null) Json.put(headers, key, val);
                }
            }

            if ((connection.getResponseCode() != HttpURLConnection.HTTP_OK)
                    && (connection.getResponseCode() != HttpURLConnection.HTTP_CREATED))
            {
                //
                // File cannot be loaded.
                //

                android.util.Log.e(LOGTAG, "getPost: ERR=" + connection.getResponseCode());

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
            android.util.Log.d(LOGTAG, ex.toString());
        }

        return null;
    }
}
