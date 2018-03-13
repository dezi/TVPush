package zz.top.p2p.camera;

import android.support.annotation.Nullable;

import android.util.Base64;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.Mac;

import zz.top.cam.Cameras;

import zz.top.p2p.base.P2P;

import zz.top.utl.Simple;
import zz.top.utl.Json;

public class P2PCloud
{
    private final static String LOGTAG = P2PCloud.class.getSimpleName();

    private final static String urlBase = "https://api.eu.xiaoyi.com/v4";

    private final static String urlLogin = urlBase + "/users/login";
    private final static String urlList = urlBase + "/devices/list";

    public final Map<String, JSONObject> cameraCache = new HashMap<>();
    public final Map<String, JSONObject> statusCache = new HashMap<>();

    private P2P p2p;

    private String loginEmail;
    private String loginPass;

    private int userId;
    private String token;
    private String token_secret;

    private JSONObject loginData;
    private JSONArray listData;

    public P2PCloud(P2P p2p)
    {
        this.p2p = p2p;
    }

    public void login(String email, String password)
    {
        loginEmail = email;
        loginPass = encryptUserPW(password);

        JSONObject params = new JSONObject();

        Json.put(params, "devType", Build.MODEL);
        Json.put(params, "password", loginPass);
        Json.put(params, "dev_os_version", "Android " + Build.VERSION.RELEASE);
        Json.put(params, "devName", Build.BRAND);
        Json.put(params, "seq", 1);
        Json.put(params, "account", loginEmail);

        P2PRestApi.getPostThreaded(urlLogin, params, false, new P2PRestApi.RestApiResultListener()
        {
            @Override
            public void OnRestApiResult(String what, JSONObject params, JSONObject result)
            {
                //Log.d(LOGTAG, "OnRestApiResult: login: " + Json.toPretty(result));

                if (Json.equals(result, "code", "20000"))
                {
                    loginData = Json.getObject(result, "data");

                    if (loginData != null)
                    {
                        userId = Json.getInt(loginData, "userid");
                        token = Json.getString(loginData, "token");
                        token_secret = Json.getString(loginData, "token_secret");

                        if ((userId > 0)
                                && (token != null) && (! token.isEmpty())
                                && (token_secret != null) && (! token_secret.isEmpty()))
                        {
                            Log.d(LOGTAG, "OnRestApiResult: login: success "
                                    + " userid=" + userId
                                    + " token=" + token
                                    + " token_secret=" + token_secret);

                            deviceList();

                            onLoginSuccess(what, params, result);

                            return;
                        }
                    }
                }

                onRestApiFailure("OnRestApiResult: login: failed.", what, params, result);
            }
        });
    }

    private void deviceList()
    {
        JSONObject params = new JSONObject();

        Json.put(params, "seq", 1);
        Json.put(params, "userid", userId);

        String key = token + "&" + token_secret;
        String query = P2PRestApi.getQueryDataString(params);
        String hmacsha1 = P2PUtil.hmacSha1(key, query);

        Log.d(LOGTAG, "deviceList: query=" + query);
        Log.d(LOGTAG, "deviceList: hmacsha1=" + hmacsha1);

        Json.put(params, "hmac", hmacsha1);

        P2PRestApi.getPostThreaded(urlList, params, false, new P2PRestApi.RestApiResultListener()
        {
            @Override
            public void OnRestApiResult(String what, JSONObject params, JSONObject result)
            {
                //Log.d(LOGTAG, "OnRestApiResult: list: " + Json.toPretty(result));

                if (Json.equals(result, "code", "20000"))
                {
                    listData = Json.getArray(result, "data");

                    if (listData != null)
                    {
                        for (int inx = 0; inx < listData.length(); inx++)
                        {
                            JSONObject device = Json.getObject(listData, inx);

                            buildCameraDescription(device);
                        }

                        Log.d(LOGTAG, "OnRestApiResult: list: success.");

                        onListSuccess(what, params, result);

                        return;
                    }
                }

                onRestApiFailure("OnRestApiResult: list: failed.", what, params, result);
            }
        });
    }

    private void onRestApiFailure(String message, String what, JSONObject params, JSONObject result)
    {
        Log.d(LOGTAG, "onRestApiFailure: message=" + message);
    }

    private void onLoginSuccess(String what, JSONObject params, JSONObject result)
    {
        Log.d(LOGTAG, "onLoginSuccess: what=" + what);
    }

    private void onListSuccess(String what, JSONObject params, JSONObject result)
    {
        Log.d(LOGTAG, "onListSuccess: what=" + what);
    }

    private void buildCameraDescription(JSONObject rawDevice)
    {
        Log.d(LOGTAG, "buildCameraDescription: json=" + Json.toPretty(rawDevice));

        JSONObject rawipcParam = Json.getObject(rawDevice, "ipcParam");

        String id = Json.getString(rawDevice, "uid");
        String name = Json.getString(rawDevice, "name");
        String nick = Json.getString(rawDevice, "nickname");
        String model = getModelName(Json.getString(rawDevice, "model"));
        String brand = "YI";
        String version = getModelName(Json.getString(rawDevice, "type"));

        String p2p_id = Json.getString(rawDevice, "uid");
        String p2p_pw = decryptDevicePW(id, Json.getString(rawDevice, "password"));
        boolean p2p_en = Json.equals(rawipcParam, "p2p_encrypt", "true");

        int cloud_id = userId;
        String cloud_em = loginEmail;
        String cloud_pw = loginPass;

        String ipaddr = Json.getString(rawipcParam, "ip");
        String ssid = Json.getString(rawipcParam, "ssid");
        String mac = Json.getString(rawipcParam, "mac");

        String type = "camera";
        String driver = "p2p";
        String capabilities = getCapabilities(Json.getString(rawDevice, "model"));

        String uuid = Simple.hmacSha1UUID(id, mac);

        Log.d(LOGTAG, "buildCameraDescription:"
                + " uuid=" + uuid
                + " p2p_id=" + p2p_id
                + " p2p_pw=" + p2p_pw);

        JSONObject camera = new JSONObject();

        JSONObject device = new JSONObject();
        Json.put(camera, "device", device);

        JSONObject credentials = new JSONObject();
        Json.put(camera, "credentials", credentials);

        JSONObject network = new JSONObject();
        Json.put(camera, "network", network);

        Json.put(device, "uuid", uuid);

        Json.put(device, "did", id);
        Json.put(device, "type", type);
        Json.put(device, "name", name);
        Json.put(device, "nick", nick);
        Json.put(device, "model", model);
        Json.put(device, "brand", brand);
        Json.put(device, "version", version);
        Json.put(device, "capabilities", capabilities);
        Json.put(device, "driver", driver);
        Json.put(device, "location", ssid);
        Json.put(device, "fixedwifi", ssid);

        Json.put(credentials, "p2p_id", p2p_id);
        Json.put(credentials, "p2p_pw", p2p_pw);
        Json.put(credentials, "p2p_en", p2p_en);

        Json.put(credentials, "cloud_id", cloud_id);
        Json.put(credentials, "cloud_em", cloud_em);
        Json.put(credentials, "cloud_pw", cloud_pw);

        Json.put(network, "mac", mac);
        Json.put(network, "wifi", ssid);
        Json.put(network, "ipaddr", ipaddr);

        Cameras.addCamera(camera);

        cameraCache.put(uuid, camera);

        p2p.onDeviceFound(camera);

        JSONObject status = new JSONObject();

        Json.put(status, "uuid", uuid);
        Json.put(status, "wifi", ssid);
        Json.put(status, "ipaddr", ipaddr);

        statusCache.put(uuid, status);

        p2p.onDeviceStatus(status);
    }

    private static String getModelName(String modelNo)
    {
        // @formatter:off

        if ( "2".equals(modelNo)) return "YI Home Cam 1080P (H21)";
        if ( "3".equals(modelNo)) return "YI Dome Cam (H19)";
        if ( "4".equals(modelNo)) return "YI Home Cam (M10/MIJIA)";
        if ( "5".equals(modelNo)) return "YI Dome Cam 1080p (H20)";
        if ( "6".equals(modelNo)) return "YI Home Cam 1080P (Y20)";
        if ( "7".equals(modelNo)) return "YI Outdoor Cam 1080p (H30)";
        if ( "9".equals(modelNo)) return "YI Home Cam Cloud Edition (Y10)";
        if ("14".equals(modelNo)) return "YI Home Cam (Y19)";

        // @formatter:on

        return "YI Home Kamera (V1/" + modelNo + ")";
    }

    private static String getCapabilities(String modelNo)
    {
        // @formatter:off

        String caps = "camera|hd|speaker|mic|fixed|tcp|wifi|stupid|ledonoff";

        if ( "2".equals(modelNo)) return caps + "|1080p|";
        if ( "3".equals(modelNo)) return caps + "|720p|pan|tilt";
        if ( "4".equals(modelNo)) return caps + "|720p";
        if ( "5".equals(modelNo)) return caps + "|1080p|pan|tilt";
        if ( "6".equals(modelNo)) return caps + "|1080p";
        if ( "7".equals(modelNo)) return caps + "|1080p";
        if ( "9".equals(modelNo)) return caps + "|720p";
        if ("14".equals(modelNo)) return caps + "|720p";

        // @formatter:on

        return caps  + "|720p";
    }

    @Nullable
    private String decryptDevicePW(String uid, String pwd)
    {
        try
        {
            byte[] key = uid.substring(0,16).getBytes();
            byte[] data = P2PUtil.getHexStringToBytes(pwd);

            Key secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher instance = Cipher.getInstance("AES/ECB/NoPadding");
            instance.init(Cipher.DECRYPT_MODE, secretKeySpec);

            return new String(instance.doFinal(data)).substring(0, 15);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    private String encryptUserPW(String password)
    {
        String key = "KXLiUdAsO81ycDyEJAeETC$KklXdz3AC";

        try
        {
            Mac instance = Mac.getInstance("HmacSHA256");
            instance.init(new SecretKeySpec(key.getBytes("UTF-8"), instance.getAlgorithm()));

            return new String(Base64.encode(instance.doFinal(password.getBytes()), 2));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }
}
