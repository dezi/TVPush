package de.xavaro.android.edx.comm;

import android.annotation.SuppressLint;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.UUID;

import de.xavaro.android.edx.base.EDX;
import de.xavaro.android.edx.simple.Simple;
import de.xavaro.android.edx.simple.Json;
import de.xavaro.android.edx.simple.Log;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class EDXCloud
{
    private static final String LOGTAG = EDXCloud.class.getSimpleName();

    private static final String CLOUDURL = "https://pg-app-c9c8cz82iexbxxdeebtxheb03v6hvs.scalabl.cloud/1";

    private static final String EDX_USEROBJECTID_PREF = "edx.userobjectid";
    private static final String EDX_SESSIONTOKEN_PREF = "edx.sessiontoken";
    private static final String EDX_INSTALLATIONID_PREF = "edx.installationid";

    private static String sessionToken;
    private static String userObjectId;
    private static String installationId;

    @SuppressLint("ApplySharedPref")
    public static void discoverDevices()
    {
        userObjectId = Simple.getPrefs().getString(EDX_USEROBJECTID_PREF, null);
        sessionToken = Simple.getPrefs().getString(EDX_SESSIONTOKEN_PREF, null);
        installationId = Simple.getPrefs().getString(EDX_INSTALLATIONID_PREF, null);

        if (! getInstallation())
        {
            Log.e(LOGTAG, "discoverDevices: getInstallation failed!");

            Simple.getPrefs().edit().remove(EDX_USEROBJECTID_PREF).commit();
            Simple.getPrefs().edit().remove(EDX_SESSIONTOKEN_PREF).commit();
            Simple.getPrefs().edit().remove(EDX_INSTALLATIONID_PREF).commit();

            return;
        }

        if (! getSession())
        {
            Log.e(LOGTAG, "discoverDevices: getSession failed!");

            Simple.getPrefs().edit().remove(EDX_USEROBJECTID_PREF).commit();
            Simple.getPrefs().edit().remove(EDX_SESSIONTOKEN_PREF).commit();

            return;
        }

        if (! getDevices())
        {
            Log.e(LOGTAG, "discoverDevices: getDevices failed!");

            //
            // Nuke all stored tokens and restart in a quick mode.
            //

            Simple.getPrefs().edit().remove(EDX_USEROBJECTID_PREF).commit();
            Simple.getPrefs().edit().remove(EDX_SESSIONTOKEN_PREF).commit();
            Simple.getPrefs().edit().remove(EDX_INSTALLATIONID_PREF).commit();

            userObjectId = null;
            sessionToken = null;
            installationId = null;

            if ((! getInstallation()) || (! getSession()) || (! getDevices()))
            {
                Log.e(LOGTAG, "discoverDevices: getDevices failed after refresh!");
            }
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

    @SuppressLint("ApplySharedPref")
    private static boolean getSession()
    {
        if ((sessionToken != null) && (userObjectId != null)) return true;

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

        if ((userObjectId == null) || (sessionToken == null)) return false;

        Simple.getPrefs().edit().putString(EDX_USEROBJECTID_PREF, userObjectId).commit();
        Simple.getPrefs().edit().putString(EDX_SESSIONTOKEN_PREF, sessionToken).commit();

        return true;
    }

    @SuppressLint("ApplySharedPref")
    private static boolean getInstallation()
    {
        if (installationId != null) return true;

        installationId = UUID.randomUUID().toString();

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

        if (installationObjectId == null) return false;

        Simple.getPrefs().edit().putString(EDX_INSTALLATIONID_PREF, installationId).commit();

        return true;
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

        //
        // Tune device name and mac addr for consistency
        // with local device response because our device
        // uuid depends on theses values.
        //

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

        JSONObject credentials = new JSONObject();

        Json.put(credentials, "uuid", uuid);
        Json.put(credentials, "localUser", "admin");
        Json.put(credentials, "localPass", localPass);

        EDX.instance.onDeviceCredentials(credentials);

        android.util.Log.d(LOGTAG, "buildDeviceDescription: credential=" + Json.toPretty(credentials));
    }
}
