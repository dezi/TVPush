package zz.top.p2p.camera;

import android.support.annotation.Nullable;
import android.util.Base64;

import java.net.URLEncoder;
import java.security.Key;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class P2PUtil
{
    private static char[] niceChars = new char[]
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

    public static String genNonce(int length)
    {
        Random rnd = new Random();

        char[] cArr = new char[length];

        for (int inx = 0; inx < length; inx++)
        {
            cArr[inx] = niceChars[Math.abs(rnd.nextInt()) % niceChars.length];
        }

        return new String(cArr);
    }

    public static String hmacSha1(String key, String data)
    {
        try
        {
            Key secretKeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
            Mac instance = Mac.getInstance("HmacSHA1");
            instance.init(secretKeySpec);

            return new String(Base64.encode(instance.doFinal(data.getBytes("UTF-8")), 2));
        }
        catch (Exception ignore)
        {
            return "";
        }
    }

    public static byte[] getHexStringToBytes(String str)
    {
        if (str == null) return null;

        int length = str.length() / 2;

        byte[] data = new byte[length];

        for (int inx = 0; inx < length; inx++)
        {
            data[inx] = (byte) Integer.parseInt(str.substring(inx * 2, (inx * 2) + 2), 16);
        }

        return data;
    }

    public static String getHexBytesToString(byte[] bytes)
    {
        return getHexBytesToString(bytes, 0, bytes.length);
    }

    public static String getHexBytesToString(byte[] bytes, boolean space)
    {
        return getHexBytesToString(bytes, 0, bytes.length, space);
    }

    public static String getHexBytesToString(byte[] bytes, int offset, int length)
    {
        return getHexBytesToString(bytes, offset, length, false);
    }

    public static String getHexBytesToString(byte[] bytes, int offset, int length, boolean space)
    {
        int clen = (length << 1) + (space ? (length - 1) : 0);

        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[ clen ];

        int pos = 0;

        for (int inx = offset; inx < (length + offset); inx++)
        {
            if (space && (inx > offset)) hexChars[ pos++ ] = ' ';

            //noinspection PointlessArithmeticExpression
            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 4) & 0x0f ];
            //noinspection PointlessBitwiseExpression
            hexChars[ pos++ ] = hexArray[ (bytes[ inx ] >> 0) & 0x0f ];
        }

        return String.valueOf(hexChars);
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
}
