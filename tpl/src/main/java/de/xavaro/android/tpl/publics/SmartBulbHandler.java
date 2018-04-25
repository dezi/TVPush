package de.xavaro.android.tpl.publics;

import android.graphics.Color;

import org.json.JSONObject;

import de.xavaro.android.pub.interfaces.pub.PUBSmartBulb;

import de.xavaro.android.tpl.handler.TPLHandlerSmartBulb;
import de.xavaro.android.tpl.simple.Simple;
import de.xavaro.android.tpl.simple.Json;
import de.xavaro.android.tpl.base.TPL;

public class SmartBulbHandler implements PUBSmartBulb
{
    private static final String LOGTAG = SmartBulbHandler.class.getSimpleName();

    private String uuid;
    private String ipaddr;

    public SmartBulbHandler(String uuid, String ipaddr)
    {
        this.uuid = uuid;
        this.ipaddr = ipaddr;
    }

    @Override
    public boolean setBulbState(final int onoff)
    {
        return setBulbHSB(-1, -1, -1, onoff);
    }

    @Override
    public boolean setBulbBrightness(int brightness)
    {
        return setBulbHSB(-1, -1, brightness, -1);
    }

    @Override
    public boolean setBulbRGB(int rgbcolor)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(rgbcolor, hsv);

        int hue = Math.round(hsv[0]);
        int saturation = Math.round(hsv[1] * 100);
        int brightness = Math.round(hsv[2] * 100);

        return setBulbHSB(hue, saturation, -1, -1);
    }

    @Override
    public boolean setBulbRGB(int rgbcolor, int brightness)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(rgbcolor, hsv);

        int hue = Math.round(hsv[0]);
        int saturation = Math.round(hsv[1] * 100);

        return setBulbHSB(hue, saturation, brightness, -1);
    }

    @Override
    public boolean setBulbRGB(int rgbcolor, int brightness, int onoff)
    {
        float[] hsv = new float[3];
        Color.colorToHSV(rgbcolor, hsv);

        int hue = Math.round(hsv[0]);
        int saturation = Math.round(hsv[1] * 100);

        return setBulbHSB(hue, saturation, brightness, onoff);
    }

    @Override
    public boolean setBulbHSB(int hue, final int saturation)
    {
        return setBulbHSB(hue, saturation, -1, -1);
    }

    @Override
    public boolean setBulbHSB(int hue, int saturation, int brightness)
    {
        return setBulbHSB(hue, saturation, brightness, -1);
    }

    @Override
    public boolean setBulbHSB(int hue, int saturation, int brightness, int onoff)
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
}
