package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.base.IOTAlive;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class GUIPingWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUIPingWizzard.class.getSimpleName();

    public GUIPingWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, false);

        setTitleIcon(R.drawable.ping_440);
        setTitleText("Ping Wizzard");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public static void collectEntries(GUILinearLayout listView, boolean todo)
    {
        JSONArray list = IOTDevices.instance.getListUUIDs();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevices.getEntry(uuid);

            if (device == null) continue;
            if (todo) continue;

            IOTStatus status = IOTStatusses.getEntry(device.uuid);
            if (status == null)continue;

            String connect = (status.ipaddr != null) ? status.ipaddr : status.macaddr;
            if (connect == null) continue;

            Long lastPing = IOTAlive.getLastPing(connect);

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onClickListener);
            entry.setTag(status);

            int residplain = GUIIcons.getImageResid(device, false);
            int residcolor = GUIIcons.getImageResid(device, true);

            entry.iconView.setImageResource(residplain);
            entry.headerViev.setText(device.name);
            entry.infoView.setText(connect);

            if (lastPing != null)
            {
                boolean pingt = (System.currentTimeMillis() - lastPing) < (20 * 1000);

                entry.setStatusColor(pingt ? GUIDefs.STATUS_COLOR_GREEN : GUIDefs.STATUS_COLOR_RED);
            }

            if (device.type.equals("smartbulb")
                    && (status.hue != null)
                    && (status.saturation != null)
                    && (status.brightness != null)
                    && (status.bulbstate != null))
            {
                int color = Simple.colorRGB(status.hue, status.saturation, 100);
                color = Simple.setRGBAlpha(color, status.brightness + 155);
                if (status.bulbstate == 0) color = GUIDefs.STATUS_COLOR_INACT;

                entry.iconView.setImageResource(residcolor, color);
            }

            if (device.type.equals("camera") && (status.ledstate != null))
            {
                int color = (status.ledstate == 0) ? GUIDefs.STATUS_COLOR_INACT : GUIDefs.STATUS_COLOR_BLUE;

                entry.iconView.setImageResource(residcolor, color);
            }

            if (device.type.equals("smartplug") && (status.plugstate != null))
            {
                int color = (status.plugstate == 0) ? GUIDefs.STATUS_COLOR_INACT : GUIDefs.STATUS_COLOR_GREEN;

                entry.iconView.setImageResource(residcolor, color);
            }

            listView.addView(entry);
        }
    }

    private static final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

        }
    };
}
