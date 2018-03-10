package zz.top.tpl.comm;

import zz.top.utl.Json;

public class TPLUDPDiscover
{
    public static String mess = "{\"system\":{\"get_sysinfo\":{}}}";

    public static void discover()
    {
        TPLUDPSender.sendMessage(Json.fromStringObject(mess));
    }
}
