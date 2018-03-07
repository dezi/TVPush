package zz.top.p2p.camera;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;

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
        //
        // https://api.eu.xiaoyi.com/v4/users/login?devType=SM-T555&password=4qZE0g6wmupcJ1yxFJZxLQXSRBH1i%2FE7mD8dp4O8ezU%3D&dev_os_version=Android+7.1.1&devName=samsung&seq=1&account=dezi%40kappa-mm.de
        // https://api.eu.xiaoyi.com/v4/users/login?devType=SM-T555&password=4qZE0g6wmupcJ1yxFJZxLQXSRBH1i%2FE7mD8dp4O8ezU%3D&dev_os_version=Android+7.1.1&devName=samsung&seq=1&account=dezi%40kappa-mm.de
        //

        JSONObject params = new JSONObject();

        Json.put(params, "devType", Build.MODEL);
        Json.put(params, "password", encryptPW(password));
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
        //
        // https://api.eu.xiaoyi.com/v4/devices/list?userid=310145&seq=1&hmac=83EhSkvYGsp4Q%2FzQMzCsF6ofDdE%3D
        //

        // data:seq=1&userid=310145
        // key:aa7d0009fa293b702d59cf31ecd55216&e84be9bbce0405d55acc3e8bd9c609af
        // result:83EhSkvYGsp4Q/zQMzCsF6ofDdE=

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
                Log.d(LOGTAG, "OnRestApiResult: list: " + Json.toPretty(result));

                if (Json.equals(result, "code", "20000"))
                {
                    listData = Json.getArray(result, "data");

                    if (listData != null)
                    {
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

    public void deviceInfo(String uid, final Runnable success, final Runnable failure)
    {

    }

    //5B7AFB8BC0DDFF7C9DF15D787EF1A9D9
    //5B7AFB8BC0DDFF7C9DF15D787EF1A9D9
    //UN75kdC3y0bx3D1

    public JSONObject getLoginData()
    {
        return loginData;
    }

    private static String urlEncode(String str)
    {
        try
        {
            return URLEncoder.encode(str, "utf-8");
        }
        catch (Exception ignore)
        {
        }

        return str;
    }

    private static String encryptPW(String password)
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
