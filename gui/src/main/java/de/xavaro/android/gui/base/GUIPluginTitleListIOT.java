package de.xavaro.android.gui.base;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIListEntryIOT;
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
        boolean blink;

        @Override
        public void run()
        {
            for (int inx = 0; inx < listView.getChildCount(); inx++)
            {
                if (! (listView.getChildAt(inx) instanceof GUIListEntryIOT)) continue;

                GUIListEntryIOT entry = (GUIListEntryIOT) listView.getChildAt(inx);

                if (entry.device == null) continue;
                if (entry.device.uuid == null) continue;
                if (entry.device.type == null) continue;

                if (!entry.device.type.equals("beacon")) continue;

                Long lastPing = IOTAlive.getAliveNetwork(entry.device.uuid);
                if (lastPing == null) continue;

                long age = (System.currentTimeMillis() - lastPing) / 1000;

                if ((age > 15) || ! blink)
                {
                    int residplain = GUIIcons.getImageResid(entry.device, false);
                    entry.iconView.setImageResource(residplain);
                }
                else
                {
                    int residcolor = GUIIcons.getImageResid(entry.device, true);
                    entry.iconView.setImageResource(residcolor, Color.RED);
                }
            }

            blink = ! blink;

            Simple.getHandler().postDelayed(onBeaconFade, 300);
        }
    };
}
