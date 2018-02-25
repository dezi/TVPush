package de.xavaro.android.yihome;

import android.util.Base64;

import java.security.Key;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class P2PUtil
{
    private static char[] NonceBase = new char[]
            {
                    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
                    'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                    'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
                    'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
                    'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                    'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
                    'w', 'x', 'y', 'z', '0', '1', '2', '3',
                    '4', '5', '6', '7', '8', '9'
            };

    public static String genNonce(int i)
    {
        char[] cArr = new char[i];
        for (int i2 = 0; i2 < i; i2++)
        {
            cArr[i2] = NonceBase[Math.abs(new Random().nextInt()) % NonceBase.length];
        }
        return new String(cArr);
    }

    public static String getPassword(String str, String str2)
    {
        String hmacSha1 = hmacSha1(str2, "user=xiaoyiuser&nonce=" + str);
        return hmacSha1.length() > 15 ? hmacSha1.substring(0, 15) : hmacSha1;
    }

    private static String hmacSha1(String str, String str2)
    {
        String str3 = "";
        try
        {
            Key secretKeySpec = new SecretKeySpec(str.getBytes("UTF-8"), "HmacSHA1");
            Mac instance = Mac.getInstance("HmacSHA1");
            instance.init(secretKeySpec);
            return new String(Base64.encode(instance.doFinal(str2.getBytes("UTF-8")), 0));
        }
        catch (Exception e)
        {
            return str3;
        }
    }
}
