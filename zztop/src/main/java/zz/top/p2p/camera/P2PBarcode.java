package zz.top.p2p.camera;

import android.util.Base64;

public class P2PBarcode
{
    private static String Encode(String str, boolean crypt)
    {
        byte[] bytes;

        if (crypt)
        {
            int length = str.length();

            String tokens = "89JFSjo8HUbhou5776NJOMp9i90ghg7Y78G78t68899y79HY7g7y87y9ED45Ew30O0jkkl";

            char[] cArr = new char[length];

            for (int inx = 0; inx < cArr.length; inx++)
            {
                cArr[inx] = '\u0000';
            }

            for (int inx = 0; inx < length; inx++)
            {
                cArr[inx] = (char) (str.charAt(inx) ^ tokens.charAt(inx % tokens.length()));

                if (cArr[inx] == '\u0000')
                {
                    cArr[inx] = str.charAt(inx);
                }
            }

            bytes = new String(cArr).getBytes();
        }
        else
        {
            bytes = str.getBytes();
        }

        return new String(Base64.encode(bytes, 0));
    }

    public static String EncodeBarcodeString(boolean noCloud, String wifiName, String wifiPass, String whatEver)
    {
        StringBuilder stringBuilder = new StringBuilder();

        String nameBase = Encode(wifiName,false);
        String passBase = Encode(wifiPass,true);

        if (noCloud)
        {
            stringBuilder.append("t=1");
        }
        else
        {
            String time = "" + (System.currentTimeMillis() / 1000);
            stringBuilder.append("b=").append(new String(Base64.encode(time.getBytes(), 0)));
        }

        stringBuilder.append("&").append("s=").append(nameBase).append("&").append("p=").append(passBase);

        if (noCloud)
        {
            stringBuilder.append("&d=").append(whatEver);
        }

        return stringBuilder.toString();
    }

}
