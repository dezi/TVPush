package de.xavaro.android.gui.base;

import android.content.Context;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.iot.base.IOTAlive;

public class GUIPluginTitleListIOT extends GUIPluginTitleList
{
    public GUIPluginTitleListIOT(Context context)
    {
        super(context);
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        Simple.getHandler().postDelayed(onBeaconFade, 100);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        Simple.getHandler().removeCallbacks(onBeaconFade);
    }

    private final Runnable onBeaconFade = new Runnable()
    {
        @Override
        public void run()
        {
            for (int inx = 0; inx < listView.getChildCount(); inx++)
            {
                GUIListEntryIOT entry = (GUIListEntryIOT) listView.getChildAt(inx);

                if (entry.device == null) continue;
                if (entry.device.uuid == null) continue;
                if (entry.device.type == null) continue;

                if (!entry.device.type.equals("beacon")) continue;

                Long lastPing = IOTAlive.getAliveNetwork(entry.device.uuid);
                if (lastPing == null) continue;

                long age = (System.currentTimeMillis() - lastPing) / 1000;
                if (age > 60) age = 60;
                int red = (int) ((60 - age) * 3) + 80;

                int color = (red << 24) + (red << 16);

                int residcolor = GUIIcons.getImageResid(entry.device, true);
                entry.iconView.setImageResource(residcolor, color);
            }

            Simple.getHandler().postDelayed(onBeaconFade, 100);
        }
    };
}
