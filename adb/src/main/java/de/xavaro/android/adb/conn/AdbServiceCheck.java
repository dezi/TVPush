package de.xavaro.android.adb.conn;

import android.content.Context;
import android.util.Log;

public class AdbServiceCheck extends AdbService
{
    private static final String LOGTAG = AdbServiceCheck.class.getSimpleName();

    public AdbServiceCheck(Context context, String ipaddr, int ipport)
    {
        super(context, ipaddr, ipport);
    }

    @Override
    protected boolean onStartService()
    {
        Log.d(LOGTAG, "onStartService: connect is ok.");

        return true;
    }
}
