package de.xavaro.android.gui.views;

import android.content.Context;
import android.graphics.Color;

import de.xavaro.android.iot.base.IOTAlive;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import de.xavaro.android.iot.things.IOTThing;
import de.xavaro.android.iot.things.IOTThings;

import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;

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

        IOTDevices.instance.subscribe(uuid, onDeviceUpdated);
        IOTStatusses.instance.subscribe(uuid, onStatusUpdated);

        Simple.getHandler().postDelayed(onBeaconBlink, 100);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        IOTDevices.instance.unsubscribe(uuid, onDeviceUpdated);
        IOTStatusses.instance.unsubscribe(uuid, onStatusUpdated);

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
        IOTThing iotThing = IOTThings.getEntry(uuid);

        if (iotThing instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotThing;
            IOTStatus status = IOTStatusses.getEntry(uuid);

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
            IOTDevice device = IOTDevices.getEntry(uuid);

            if ((device != null)
                && (device.uuid != null)
                && (device.type != null)
                && device.type.equals("beacon"))
            {
                Long lastPing = IOTAlive.getAliveNetwork(device.uuid);

                if (lastPing != null)
                {
                    long age = (System.currentTimeMillis() - lastPing) / 1000;

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
