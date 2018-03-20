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
import de.xavaro.android.gui.views.GUISeparatorView;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class GUILocationsWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUILocationsWizzard.class.getSimpleName();

    public GUILocationsWizzard(Context context)
    {
        super(context);

        setTitleIcon(R.drawable.todo_list_512);
        setTitleText("Verortung der Dinge");

        Simple.getHandler().post(makeEntryList);
    }

    private final Runnable makeEntryList = new Runnable()
    {
        @Override
        public void run()
        {
            listView.removeAllViews();

            collectEntries(listView, false);

            Simple.getHandler().postDelayed(makeEntryList, 10 * 1000);
        }
    };

    private static void collectEntries(GUILinearLayout listView, boolean todo)
    {
        JSONArray list = IOTDevices.instance.getListUUIDs();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevices.getEntry(uuid);

            if (device == null) continue;

            if (! device.hasCapability("fixed")) continue;

            boolean isnice = (device.fixedLatFine != null)
                    && (device.fixedLonFine != null)
                    && (device.fixedAltFine != null);

            if (todo && isnice) continue;

            if (listView.getChildCount() > 0)
            {
                listView.addView(new GUISeparatorView(listView.getContext()));
            }

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onLocationSetClickListener);
            entry.setTag(uuid);

            String info = "Keine Position";

            if (isnice)
            {
                info = ""
                        + Simple.getRounded(device.fixedLatFine)
                        + " "
                        + Simple.getRounded(device.fixedLonFine)
                        + " "
                        + Simple.getRounded(device.fixedAltFine)
                        + " m";
            }

            entry.iconView.setImageResource(GUIIcons.getImageResid(device));
            entry.headerViev.setText(device.name);
            entry.infoView.setText(info);

            entry.infoView.setTextColor(isnice
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);

            listView.addView(entry);
        }
    }

    private static final OnClickListener onLocationSetClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
        }
    };
}
