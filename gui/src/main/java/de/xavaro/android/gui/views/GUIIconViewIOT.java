package de.xavaro.android.gui.views;

import android.content.Context;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.iot.base.IOTObject;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;

public class GUIIconViewIOT extends GUIIconView
{
    private static final String LOGTAG = GUIIconViewIOT.class.getSimpleName();

    public IOTObject iotObject;

    public GUIIconViewIOT(Context context)
    {
        super(context);
    }

    public void setIOTObject(IOTObject iotObject)
    {
        this.iotObject = iotObject;

        updateContents();
    }

    public void updateContents()
    {
        Log.d(LOGTAG, "updateContents: ##########" + iotObject.toJsonString());

        if (iotObject instanceof IOTDevice)
        {
            IOTDevice device = (IOTDevice) iotObject;
            IOTStatus status = IOTStatusses.getEntry(device.uuid);

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
}
