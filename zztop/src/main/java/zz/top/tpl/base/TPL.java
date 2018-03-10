package zz.top.tpl.base;

import android.content.Context;

import zz.top.tpl.comm.TPLMessageHandler;
import zz.top.tpl.comm.TPLMessageService;
import zz.top.tpl.handler.TPLHandlerSysInfo;

public class TPL
{
    public static TPLMessageHandler message;

    public static void initialize(Context context)
    {
        TPLMessageHandler.initialize();
        TPLMessageService.startService();

        TPLHandlerSysInfo.sendSysInfoBroadcast();
    }
}
