package de.xavaro.android.tvpush;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;

public class SystemsIOT extends IOT
{
    private static final String LOGTAG = SystemsIOT.class.getSimpleName();

    public SystemsIOT(Application appcontext)
    {
        super(appcontext);
    }

    @Override
    public void onDeviceFound(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceFound:");
    }

    @Override
    public void onDeviceAlive(JSONObject device)
    {
        Log.d(LOGTAG, "onDeviceAlive:");
    }
}
