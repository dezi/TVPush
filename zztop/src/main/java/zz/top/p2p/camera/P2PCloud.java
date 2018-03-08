package zz.top.p2p.camera;

import android.support.annotation.Nullable;

import android.util.Base64;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.Mac;

import zz.top.cam.Cameras;
import zz.top.utl.Json;

public class P2PCloud
{
    public static final String LOGTAG = P2PCloud.class.getSimpleName();

    private static final String urlBase = "https://api.eu.xiaoyi.com/v4";

    private static final String urlLogin = urlBase + "/users/login";
    private static final String urlList = urlBase + "/devices/list";

    private String loginEmail;
    private String loginPass;
    private int loginUserId;

    private JSONObject loginData;
    private JSONArray listData;

    public P2PCloud(String email)
    {
        loginEmail = email;
    }

    public void login(String password)
    {
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
                Log.d(LOGTAG, "OnRestApiResult: login: " + Json.toPretty(result));

                if (Json.equals(result, "code", "20000"))
                {
                    loginData = Json.getObject(result, "data");

                    if (loginData != null)
                    {
                        loginUserId = Json.getInt(loginData, "userid");

                        if (loginUserId > 0)
                        {
                            Log.d(LOGTAG, "OnRestApiResult: login: success.");

                            deviceList();

                            return;
                        }
                    }
                }

                onLoginFailure("OnRestApiResult: login: failed.");
            }
        });
    }

    public void deviceList()
    {
        int userid = Json.getInt(loginData, "userid");
        String token = Json.getString(loginData, "token");
        String token_secret = Json.getString(loginData, "token_secret");

        Log.d(LOGTAG, "deviceList:"
                + " userid=" + userid
                + " token=" + token
                + " token_secret=" + token_secret);

        if ((userid == 0) || (token == null) || (token_secret == null))
        {
            onLoginFailure("OnRestApiResult: list: no credentials found.");

            return;
        }

        JSONObject params = new JSONObject();

        Json.put(params, "seq", 1);
        Json.put(params, "userid", userid);

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
                Log.d(LOGTAG, "OnRestApiResult: list: " + Json.toPretty(result));

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

                        onLoginSuccess();

                        return;
                    }
                }

                onLoginFailure("OnRestApiResult: list: failed.");
            }
        });
    }

    protected void onLoginFailure(String message)
    {
        Log.d(LOGTAG, message);
    }

    protected void onLoginSuccess()
    {
        Log.d(LOGTAG, "Login success.");
    }

    private void buildCameraDescription(JSONObject rawDevice)
    {
        JSONObject rawipcParam = Json.getObject(rawDevice, "ipcParam");

        String id = Json.getString(rawDevice, "uid");
        String name = Json.getString(rawDevice, "name");
        String nick = Json.getString(rawDevice, "nickname");
        String model = getModelName(Json.getString(rawDevice, "model"));
        String version = getModelName(Json.getString(rawDevice, "type"));

        String p2p_id = Json.getString(rawDevice, "uid");
        String p2p_pw = decryptDevicePW(id, Json.getString(rawDevice, "password"));
        boolean p2p_en = Json.equals(rawipcParam, "p2p_encrypt", "true");

        int cloud_id = loginUserId;
        String cloud_em = loginEmail;
        String cloud_pw = loginPass;

        String ip = Json.getString(rawipcParam, "ip");
        String ssid = Json.getString(rawipcParam, "ssid");
        String mac = Json.getString(rawipcParam, "mac");

        String category = "camera";
        String driver = "yi-p2p";
        String capability = getCapabilities(Json.getString(rawDevice, "model"));

        String uuid = P2PUtil.hmacSha1UUID(id, mac);

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

        Json.put(device, "id", id);
        Json.put(device, "name", name);
        Json.put(device, "nick", nick);
        Json.put(device, "model", model);
        Json.put(device, "version", version);
        Json.put(device, "category", category);
        Json.put(device, "capability", capability);
        Json.put(device, "driver", driver);

        Json.put(credentials, "p2p_id", p2p_id);
        Json.put(credentials, "p2p_pw", p2p_pw);
        Json.put(credentials, "p2p_en", p2p_en);

        Json.put(credentials, "cloud_id", cloud_id);
        Json.put(credentials, "cloud_em", cloud_em);
        Json.put(credentials, "cloud_pw", cloud_pw);

        Json.put(network, "ip", ip);
        Json.put(network, "ssid", ssid);
        Json.put(network, "mac", mac);

        Cameras.addCamera(camera);
    }

    public static String getModelName(String modelNo)
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

    public static String getCapabilities(String modelNo)
    {
        // @formatter:off

        if ( "2".equals(modelNo)) return "camera|hd|speaker|mic|fixed|1080p|";
        if ( "3".equals(modelNo)) return "camera|hd|speaker|mic|fixed|720p|pan|tilt";
        if ( "4".equals(modelNo)) return "camera|hd|speaker|mic|fixed|720p";
        if ( "5".equals(modelNo)) return "camera|hd|speaker|mic|fixed|1080p|pan|tilt";
        if ( "6".equals(modelNo)) return "camera|hd|speaker|mic|fixed|1080p";
        if ( "7".equals(modelNo)) return "camera|hd|speaker|mic|fixed|1080p";
        if ( "9".equals(modelNo)) return "camera|hd|speaker|mic|fixed|720p";
        if ("14".equals(modelNo)) return "camera|hd|speaker|mic|fixed|720p";

        // @formatter:on

        return "camera|hd|speaker|mic|fixed|720p";
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
