package de.xavaro.android.systems;

import android.app.Application;
import android.util.Log;

import org.json.JSONObject;

import de.xavaro.android.iot.base.IOT;
import zz.top.utl.Json;

public class SystemsIOT extends IOT
{
    private static final String LOGTAG = SystemsIOT.class.getSimpleName();

    public SystemsIOT(Application appcontext)
    {
        super(appcontext);
    }

    @Override
    public boolean onDeviceStatusRequest(JSONObject iotDevice)
    {
        String uuid = Json.getString(iotDevice, "uuid");
        String driver = Json.getString(iotDevice, "driver");

        Log.d(LOGTAG, "onDeviceStatusRequest: uuid=" + uuid + " driver=" + driver);

        if ((uuid == null) || (driver == null)) return false;

        if (driver.equals("p2p"))
        {

        }

        if (driver.equals("tpl"))
        {
            return SystemsTPL.instance.putDeviceStatusRequest(iotDevice);
        }

        if (driver.equals("sny"))
        {

        }

        return false;
    }
}
