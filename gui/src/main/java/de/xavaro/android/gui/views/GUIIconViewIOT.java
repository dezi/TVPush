package de.xavaro.android.gui.views;

import android.content.Context;
import android.graphics.Color;

import de.xavaro.android.iot.base.IOT;
import de.xavaro.android.iot.base.IOTAlive;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTThing;

import de.xavaro.android.iot.status.IOTStatus;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIIcons;

import de.xavaro.android.gui.simple.Simple;

public class GUIIconViewIOT extends GUIIconView
{
    private static final String LOGTAG = GUIIconViewIOT.class.getSimpleName();

    public String uuid;

    public GUIIconViewIOT(Context context)
    {
        super(context);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        IOTDevice.list.subscribe(uuid, onDeviceUpdated);
        IOTStatus.list.subscribe(uuid, onStatusUpdated);

        Simple.getHandler().postDelayed(onBeaconBlink, 100);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        IOTDevice.list.unsubscribe(uuid, onDeviceUpdated);
        IOTStatus.list.unsubscribe(uuid, onStatusUpdated);

        Simple.getHandler().removeCallbacks(onBeaconBlink);
    }

    public void setIOTThing(String uuid)
    {
        this.uuid = uuid;

        updateContent();
    }

    private final Runnable onDeviceUpdated = new Runnable()
    {
        @Override
        public void run()
        {
            updateContent();
        }
    };

    private final Runnable onStatusUpdated = new Runnable()
    {
        @Override
        public void run()
        {
            updateContent();
        }
    };

    private void updateContent()
    {
        IOTThing iotThing = IOTThing.getEntry(uuid);

        if (iotThing instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotThing;
            IOTStatus status = IOTStatus.list.getEntry(uuid);

            int residplain = GUIIcons.getImageResid(device, false);
            int residcolor = GUIIcons.getImageResid(device, true);

            if (device.type.equals("smartbulb"))
            {
                int color = GUIDefs.STATUS_COLOR_INACT;

                if ((status != null)
                        && (status.hue != null)
                        && (status.saturation != null)
                        && (status.brightness != null)
                        && (status.bulbstate != null))
                {
                    if (status.bulbstate != 0)
                    {
                        color = Simple.colorRGB(status.hue, status.saturation, 100);
                        color = Simple.setRGBAlpha(color, status.brightness + 155);
                    }
                }

                setImageResource(residcolor, color);

                return;
            }

            if (device.type.equals("smartplug"))
            {
                int color = ((status == null) || (status.plugstate == null) || (status.plugstate == 0))
                        ? GUIDefs.STATUS_COLOR_INACT
                        : GUIDefs.STATUS_COLOR_GREEN;

                setImageResource(residcolor, color);

                return;
            }

            if (device.type.equals("camera"))
            {
                int color = ((status == null) || (status.ledstate == null) || (status.ledstate == 0))
                        ? GUIDefs.STATUS_COLOR_INACT
                        : GUIDefs.STATUS_COLOR_BLUE;

                setImageResource(residcolor, color);

                return;
            }

            setImageResource(residplain);
        }
    }

    private final Runnable onBeaconBlink = new Runnable()
    {
        private boolean blink;

        @Override
        public void run()
        {
            IOTDevice device = IOTDevice.list.getEntry(uuid);

            if ((device != null)
                && (device.uuid != null)
                && (device.type != null)
                && device.type.equals("beacon"))
            {
                Long lastAlive = (IOT.instance.alive != null) ? IOT.instance.alive.getAlive(device.uuid) : null;

                if ((lastAlive != null) && (lastAlive > 0))
                {
                    long age = (System.currentTimeMillis() - lastAlive) / 1000;

                    if ((age > 15) || !blink)
                    {
                        int residplain = GUIIcons.getImageResid(device, false);
                        setImageResource(residplain);
                    }
                    else
                    {
                        int residcolor = GUIIcons.getImageResid(device, true);
                        setImageResource(residcolor, Color.RED);
                    }
                }
            }

            blink = !blink;

            Simple.getHandler().postDelayed(onBeaconBlink, 300);
        }
    };
}
