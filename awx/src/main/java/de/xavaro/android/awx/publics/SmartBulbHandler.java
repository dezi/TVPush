package de.xavaro.android.awx.publics;

import de.xavaro.android.awx.comm.AWXDevice;
import de.xavaro.android.pub.interfaces.pub.PUBSmartBulb;
import de.xavaro.android.awx.simple.Simple;

public class SmartBulbHandler implements PUBSmartBulb
{
    private static final String LOGTAG = SmartBulbHandler.class.getSimpleName();

    AWXDevice awxdevice;
    private String uuid;
    private String meshname;
    private short meshid;

    public SmartBulbHandler(String uuid, String meshname, short meshid)
    {
        this.uuid = uuid;
        this.meshname = meshname;
        this.meshid = meshid;

        awxdevice = AWXDevice.findDevice(meshid);
    }

    @Override
    public boolean setBulbState(final int onoff)
    {
        return setBulb(null, null, onoff);
    }

    @Override
    public boolean setBulbBrightness(int brightness)
    {
        return setBulb(null, brightness, null);
    }

    @Override
    public boolean setBulbRGB(int rgbcolor)
    {
        return setBulb(rgbcolor, null, null);
    }

    @Override
    public boolean setBulbRGB(int rgbcolor, int brightness)
    {
        return setBulb(rgbcolor, brightness, null);
    }

    @Override
    public boolean setBulbRGB(int rgbcolor, int brightness, int onoff)
    {
        return setBulb(rgbcolor, brightness, null);
    }

    @Override
    public boolean setBulbHSB(int hue, final int saturation)
    {
        int rgbcolor = Simple.colorRGB(hue, saturation, 100);

        return setBulb(rgbcolor, null , null);
    }

    @Override
    public boolean setBulbHSB(int hue, int saturation, int brightness)
    {
        int rgbcolor = Simple.colorRGB(hue, saturation, 100);

        return setBulb(rgbcolor, brightness, null);
    }

    @Override
    public boolean setBulbHSB(int hue, int saturation, int brightness, int onoff)
    {
        int rgbcolor = Simple.colorRGB(hue, saturation, 100);

        return setBulbRGB(rgbcolor, brightness, onoff);
    }

    private boolean setBulb(Integer rgbcolor, Integer brightness, Integer onoff)
    {
        if (awxdevice == null) return false;

        if ((brightness != null) && (brightness > 100)) brightness = 100;

        final Integer cbonoff = onoff;
        final Integer cbrgbcolor = rgbcolor;
        final Integer cbbrightness = brightness;

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (cbonoff != null) awxdevice.setPowerState(cbonoff);
                if (cbrgbcolor != null) awxdevice.setColor(cbrgbcolor);
                if (cbbrightness != null) awxdevice.setColorBrighness(cbbrightness);
                if (cbbrightness != null) awxdevice.setWhiteBrighness(cbbrightness);

                awxdevice.executeNext();
            }
        };

        Simple.runBackground(runnable);

        return true;
    }

    public boolean getState()
    {
        if (awxdevice == null) return false;

        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                awxdevice.getStatus();
                awxdevice.executeNext();
            }
        };

        Simple.runBackground(runnable);

        return true;
    }
}
