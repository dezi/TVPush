package zz.top.tpl.base;

import zz.top.tpl.comm.TPLMessageHandler;
import zz.top.tpl.comm.TPLMessageService;

public class TPL
{
    public static TPLCloud cloud;
    public static TPLMessageHandler message;

    static
    {
        TPLMessageHandler.initialize();
        TPLMessageService.startService();
    }
}
