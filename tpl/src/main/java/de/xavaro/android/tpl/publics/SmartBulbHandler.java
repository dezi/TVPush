package de.xavaro.android.tpl.publics;

import org.json.JSONObject;

import pub.android.interfaces.pub.PUBSmartBulb;

import de.xavaro.android.tpl.handler.TPLHandler;
import de.xavaro.android.tpl.base.TPL;
import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.simple.Simple;

import de.xavaro.android.tpl.handler.TPLHandlerSmartBulb;

public class SmartBulbHandler implements PUBSmartBulb
{
    private static final String LOGTAG = TPLHandler.class.getSimpleName();

    private String uuid;
    private String ipaddr;

    public SmartBulbHandler(String uuid, String ipaddr)
    {
        this.uuid = uuid;
        this.ipaddr = ipaddr;
    }

    @Override
    public boolean setBulb(int onoff, int hue, int saturation, int brightness)
    {
        if (hue > 360) hue = 360;
        if (saturation > 100) saturation = 100;
        if (brightness > 100) brightness = 100;

        final int cbonoff = onoff;
        final int cbhue = hue;
        final int cbsaturation = saturation;
        final int cbbrightness = brightness;

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (TPLHandlerSmartBulb.sendBulb(ipaddr, cbonoff, cbhue, cbsaturation, cbbrightness))
                {
                    JSONObject status = new JSONObject();

                    Json.put(status, "uuid", uuid);

                    if (cbonoff >= 0) Json.put(status, "bulbstate", cbonoff);
                    if (cbhue >= 0) Json.put(status, "hue", cbhue);
                    if (cbsaturation >= 0) Json.put(status, "saturation", cbsaturation);
                    if (cbbrightness >= 0) Json.put(status, "brightness", cbbrightness);

                    TPL.instance.onDeviceStatus(status);
                }
            }
        };

        Simple.runBackground(runnable);

        return true;
    }

    @Override
    public boolean setBulbState(final int onoff)
    {
        return setBulb(onoff, -1, -1, -1);
    }

    @Override
    public boolean setBulbHSB(int hue, int saturation, int brightness)
    {
        return setBulb(-1, hue, saturation, brightness);
    }

    @Override
    public boolean setBulbHSOnly(int hue, final int saturation)
    {
        return setBulb(-1, hue, saturation, -1);
    }

    @Override
    public boolean setBulbBrightness(int brightness)
    {
        return setBulb(-1, -1, -1, brightness);
    }
}
