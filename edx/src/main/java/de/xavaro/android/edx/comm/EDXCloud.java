package de.xavaro.android.edx.comm;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import de.xavaro.android.edx.base.EDX;
import de.xavaro.android.edx.simple.Simple;
import de.xavaro.android.edx.simple.Json;
import de.xavaro.android.edx.simple.Log;

public class EDXCloud
{
    private static final String LOGTAG = EDXCloud.class.getSimpleName();

    private static final String CLOUDURL = "https://pg-app-c9c8cz82iexbxxdeebtxheb03v6hvs.scalabl.cloud/1";

    private static String sessionToken;
    private static String userObjectId;
    private static String installationId = UUID.randomUUID().toString();

    public static void discoverDevices()
    {
        if (! getInstallation())
        {
            Log.e(LOGTAG, "discoverDevices: getInstallation failed!");
            return;
        }

        if (! getSession())
        {
            Log.e(LOGTAG, "discoverDevices: getSession failed!");
            return;
        }

        if (! getDevices())
        {
            Log.e(LOGTAG, "discoverDevices: getDevices failed!");
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

        String url = CLOUDURL + "/classes/Device";

        String resultStr = EDXPostCloud.getPost(url, body.toString(), sessionToken);
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

        String url = CLOUDURL + "/login";

        String resultstr = EDXPostCloud.getPost(url, body.toString(), sessionToken);
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

        String url = CLOUDURL + "/classes/_Installation";

        String resultStr = EDXPostCloud.getPost(url, body, sessionToken);
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
        String model = Json.getString(edidev, "modelNumber");
        String pnvId = Json.getString(edidev, "pnvId");
        String version = Json.getString(edidev, "fwVer");
        String macaddr = Json.getString(edidev, "deviceMac");
        String localPass = Json.getString(edidev, "localPass");

        if ((name == null)
                || (model == null)
                || (pnvId == null)
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
        Json.put(device, "did", pnvId);

        Json.put(device, "capabilities", caps);

        EDX.instance.onDeviceFound(edimax);

        android.util.Log.d(LOGTAG, "buildDeviceDescription: device=" + Json.toPretty(edimax));

        JSONObject credential = new JSONObject();
        JSONObject credentials = new JSONObject();

        Json.put(credential, "uuid", uuid);
        Json.put(credential, "credentials", credentials);
        Json.put(credentials, "localUser", "admin");
        Json.put(credentials, "localPass", localPass);

        EDX.instance.onDeviceCredentials(credential);

        android.util.Log.d(LOGTAG, "buildDeviceDescription: credential=" + Json.toPretty(credential));
    }
}
