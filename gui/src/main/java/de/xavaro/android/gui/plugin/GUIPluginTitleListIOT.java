package de.xavaro.android.gui.plugin;

import android.content.Context;
import android.graphics.Color;

import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.simple.Simple;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

import de.xavaro.android.iot.base.IOTAlive;

public class GUIPluginTitleListIOT extends GUIPluginTitleList
{
    private final static String LOGTAG = GUIPluginTitleListIOT.class.getSimpleName();

    public GUIPluginTitleListIOT(Context context)
    {
        super(context);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        Simple.getHandler().postDelayed(onBeaconBlink, 100);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        Simple.getHandler().removeCallbacks(onBeaconBlink);
    }

    private final Runnable onBeaconBlink = new Runnable()
    {
        private boolean blink;

        @Override
        public void run()
        {
            for (int inx = 0; inx < listView.getChildCount(); inx++)
            {
                if (! (listView.getChildAt(inx) instanceof GUIListEntryIOT)) continue;

                GUIListEntryIOT entry = (GUIListEntryIOT) listView.getChildAt(inx);
                IOTDevice device = IOTDevices.getEntry(entry.uuid);

                if (device == null) continue;
                if (device.uuid == null) continue;
                if (device.type == null) continue;

                if (!device.type.equals("beacon")) continue;

                Long lastPing = IOTAlive.getAliveNetwork(device.uuid);
                if (lastPing == null) continue;

                long age = (System.currentTimeMillis() - lastPing) / 1000;

                if ((age > 15) || ! blink)
                {
                    int residplain = GUIIcons.getImageResid(device, false);
                    entry.iconView.setImageResource(residplain);
                }
                else
                {
                    int residcolor = GUIIcons.getImageResid(device, true);
                    entry.iconView.setImageResource(residcolor, Color.RED);
                }
            }

            blink = ! blink;

            Simple.getHandler().postDelayed(onBeaconBlink, 300);
        }
    };
}
