package de.xavaro.android.brl.comm;

@SuppressWarnings({"WeakerAccess", "unused"})
public class BRLUtil
{
    public static String getCapabilities(String model)
    {
        if (model.startsWith("A1"))
        {
            return "airquality|fixed|tcp|wifi|stupid";
        }

        if (model.startsWith("MP1"))
        {
            return "smartplug|fixed|tcp|wifi|stupid|timer|plugonoff";
        }

        if (model.startsWith("RM"))
        {
            return "irremote|fixed|tcp|wifi|stupid";
        }

        if (model.startsWith("Smart Plug"))
        {
            return "smartplug|fixed|tcp|wifi|stupid|plugonoff";
        }

        return "unknown|fixed|tcp|wifi|stupid";
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
}
