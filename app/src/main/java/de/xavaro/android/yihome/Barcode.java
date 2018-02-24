package de.xavaro.android.yihome;

import android.util.Base64;

public class Barcode
{
    private static String Encode(String str, boolean crypt)
    {
        byte[] bytes;
        int i = 0;

        if (crypt)
        {
            int length = str.length();
            int length2 = "89JFSjo8HUbhou5776NJOMp9i90ghg7Y78G78t68899y79HY7g7y87y9ED45Ew30O0jkkl".length();
            char[] cArr = new char[length];
            for (int i2 = 0; i2 < cArr.length; i2++)
            {
                cArr[i2] = '\u0000';
            }
            while (i < length)
            {
                cArr[i] = (char) (str.charAt(i) ^ "89JFSjo8HUbhou5776NJOMp9i90ghg7Y78G78t68899y79HY7g7y87y9ED45Ew30O0jkkl".charAt(i % length2));
                if (cArr[i] == '\u0000')
                {
                    cArr[i] = str.charAt(i);
                }
                i++;
            }
            bytes = new String(cArr).getBytes();
        }
        else
        {
            bytes = str.getBytes();
        }

        return new String(Base64.encode(bytes, 2));
    }

    public static byte[] longToByteArray(long value)
    {
        return new byte[]{
                (byte) (value >> 56),
                (byte) (value >> 48),
                (byte) (value >> 40),
                (byte) (value >> 32),
                (byte) (value >> 24),
                (byte) (value >> 16),
                (byte) (value >> 8),
                (byte) value
        };
    }

    public static String EncodeBarcodeString(boolean z, String str)
    {
        StringBuilder stringBuilder = new StringBuilder();

        String a = Encode("Dezi Home", false);
        String a2 = Encode("1234abcd", true);

        if (z)
        {
            stringBuilder.append("t=1");
        }
        else
        {
            String time = "" + (System.currentTimeMillis() / 1000);
            stringBuilder.append("b=").append(new String(Base64.encode(time.getBytes(), 2)));
        }

        stringBuilder.append("&").append("s=").append(a).append("&").append("p=").append(a2);

        if (z)
        {
            stringBuilder.append("&d=").append(str);
        }

        return stringBuilder.toString();
    }

}
