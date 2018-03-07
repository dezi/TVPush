package zz.top.p2p.camera;

import android.support.annotation.Nullable;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import zz.top.utl.Json;

public class P2PLogin
{
    public static final String LOGTAG = P2PLogin.class.getSimpleName();

    private static final String urlBase = "https://api.eu.xiaoyi.com/v4";

    private static final String urlLogin = urlBase + "/users/login";
    private static final String urlList = urlBase + "/devices/list";

    public String loginEmail;

    public JSONObject loginData;
    public JSONArray listData;

    public P2PLogin(String email)
    {
        loginEmail = email;
    }

    public void login(String password, final Runnable success, final Runnable failure)
    {
        JSONObject params = new JSONObject();

        Json.put(params, "devType", Build.MODEL);
        Json.put(params, "password", encryptUserPW(password));
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
                        Log.d(LOGTAG, "OnRestApiResult: login: success.");

                        if (success != null) success.run();

                        return;
                    }
                }

                if (failure != null) failure.run();

                Log.d(LOGTAG, "OnRestApiResult: login: failed.");
            }
        });
    }

    public void deviceList(final Runnable success, final Runnable failure)
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
            failure.run();

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
                //Log.d(LOGTAG, "OnRestApiResult: list: " + Json.toPretty(result));

                if (Json.equals(result, "code", "20000"))
                {
                    listData = Json.getArray(result, "data");

                    if (listData != null)
                    {
                        for (int inx = 0; inx < listData.length(); inx++)
                        {
                            JSONObject device = Json.getObject(listData, inx);

                            String p2p_id = Json.getString(device, "uid");
                            String p2p_pw = Json.getString(device, "password");

                            p2p_pw = decryptDevicePW(p2p_id, p2p_pw);

                            Json.put(device, "p2p_id", p2p_id);
                            Json.put(device, "p2p_pw", p2p_pw);

                            Log.d(LOGTAG, "OnRestApiResult: list: p2p_id=" + p2p_id + " p2p_pw=" + p2p_pw);
                        }

                        Log.d(LOGTAG, "OnRestApiResult: list: success.");

                        if (success != null) success.run();

                        return;
                    }
                }

                if (failure != null) failure.run();

                Log.d(LOGTAG, "OnRestApiResult: list: failed.");
            }
        });
    }

    @Nullable
    public static String decryptDevicePW(String uid, String pwd)
    {
        try
        {
            byte[] key = uid.substring(0,16).getBytes();
            byte[] data = P2PUtil.getHexStringToBytes(pwd);

            Key secretKeySpec = new SecretKeySpec(key, "AES");
            Cipher instance = Cipher.getInstance("AES/ECB/NoPadding");
            instance.init(Cipher.DECRYPT_MODE, secretKeySpec);

            return new String(instance.doFinal(data));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    @Nullable
    private static String encryptUserPW(String password)
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
