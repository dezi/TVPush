package de.xavaro.android.gui.views;

import android.content.Context;
import android.util.Log;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.iot.base.IOTAlive;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;

public class GUIListEntryIOT extends GUIListEntry
{
    private final static String LOGTAG = GUIListEntryIOT.class.getSimpleName();

    public String uuid;
    public IOTDevice device;
    public IOTStatus status;
    public IOTCredential credential;

    public GUIListEntryIOT(Context context)
    {
        super(context);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        IOTStatusses.instance.subscribe(device.uuid, onStatusUpdated);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        IOTStatusses.instance.unsubscribe(device.uuid, onStatusUpdated);
    }

    public void updateContent()
    {
        int residplain = GUIIcons.getImageResid(device, false);
        int residcolor = GUIIcons.getImageResid(device, true);

        iconView.setImageResource(residplain);

        if (device.type.equals("smartbulb")
                && (status.hue != null)
                && (status.saturation != null)
                && (status.brightness != null)
                && (status.bulbstate != null))
        {
            int color = Simple.colorRGB(status.hue, status.saturation, 100);
            color = Simple.setRGBAlpha(color, status.brightness + 155);
            if (status.bulbstate == 0) color = GUIDefs.STATUS_COLOR_INACT;

            iconView.setImageResource(residcolor, color);
        }

        if (device.type.equals("camera") && (status.ledstate != null))
        {
            int color = (status.ledstate == 0) ? GUIDefs.STATUS_COLOR_INACT : GUIDefs.STATUS_COLOR_BLUE;

            iconView.setImageResource(residcolor, color);
        }

        if (device.type.equals("smartplug") && (status.plugstate != null))
        {
            int color = (status.plugstate == 0) ? GUIDefs.STATUS_COLOR_INACT : GUIDefs.STATUS_COLOR_GREEN;

            iconView.setImageResource(residcolor, color);
        }

        String connect = (status.ipaddr != null) ? status.ipaddr : status.macaddr;

        headerViev.setText(device.name);
        infoView.setText(connect);

        Long lastPing = IOTAlive.getAliveNetwork(uuid);

        if (lastPing != null)
        {
            boolean pingt = (System.currentTimeMillis() - lastPing) < (60 * 1000);
            setStatusColor(pingt ? GUIDefs.STATUS_COLOR_GREEN : GUIDefs.STATUS_COLOR_RED);
        }
    }

    private final Runnable onStatusUpdated = new Runnable()
    {
        @Override
        public void run()
        {
            Log.d(LOGTAG, "onStatusUpdated: name=" + device.name);

            status = IOTStatusses.getEntry(uuid);
            
            updateContent();
        }
    };
}
