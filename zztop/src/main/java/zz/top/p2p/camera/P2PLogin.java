package zz.top.p2p.camera;

import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.net.URLEncoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class P2PLogin
{
    public static final String LOGTAG = P2PLogin.class.getSimpleName();

    private static final String url = "https://api.eu.xiaoyi.com/v4/users/login";

    public static boolean login(String email, String password)
    {
        //
        // https://api.eu.xiaoyi.com/v4/users/login?devType=SM-T555&password=4qZE0g6wmupcJ1yxFJZxLQXSRBH1i%2FE7mD8dp4O8ezU%3D&dev_os_version=Android+7.1.1&devName=samsung&seq=1&account=dezi%40kappa-mm.de
        // https://api.eu.xiaoyi.com/v4/users/login?devType=SM-T555&password=4qZE0g6wmupcJ1yxFJZxLQXSRBH1i%2FE7mD8dp4O8ezU%3D&dev_os_version=Android+7.1.1&devName=samsung&seq=1&account=dezi%40kappa-mm.de
        //

        String urlstr = url
                + "?" + "devType=" + urlEncode(Build.MODEL)
                + "&" + "password=" + urlEncode(encryptPW(password))
                + "&" + "dev_os_version=" + urlEncode("Android " + Build.VERSION.RELEASE)
                + "&" + "devName=" + urlEncode(Build.BRAND)
                + "&" + "seq=" + urlEncode("1")
                + "&" + "account=" + urlEncode(email)
                ;

        Log.d(LOGTAG, "login: url=" + urlstr);

        return true;
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
