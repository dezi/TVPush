package de.xavaro.android.awx.comm;

import java.nio.charset.Charset;
import java.util.Locale;
import java.util.UUID;

public class AWXHardwareUtils
{
    public static String getUuid(String address)
    {
        return UUID.nameUUIDFromBytes(address.replace("-", "").replace(":", "").toLowerCase(Locale.US).getBytes(Charset.forName("UTF-8"))).toString();
    }

    public static String getAddress(byte[] address)
    {
        return String.format(Locale.US, "%02X:%02X:%02X:%02X:%02X:%02X", new Object[]{
                Byte.valueOf(address[0]),
                Byte.valueOf(address[1]),
                Byte.valueOf(address[2]),
                Byte.valueOf(address[3]),
                Byte.valueOf(address[4]),
                Byte.valueOf(address[5])});
    }

    public static byte[] getAddress(String address)
    {
        String[] digits = address.split(":");
        byte[] bytes = new byte[digits.length];
        for (int i = 0; i < digits.length; i++)
        {
            bytes[i] = (byte) Integer.parseInt(digits[i], 16);
        }
        return bytes;
    }

    public static short getMeshId(String address)
    {
        int meshId = Integer.parseInt(address.substring(12, 14) + address.substring(15, 17), 16);
        if (meshId == 0 || meshId == 32768)
        {
            meshId = 1;
        }
        else
        {
            meshId %= 32768;
        }
        return (short) meshId;
    }
}
