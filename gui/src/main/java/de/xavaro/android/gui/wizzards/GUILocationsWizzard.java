package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.R;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;

import de.xavaro.android.iot.things.IOTDevice;

public class GUILocationsWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUILocationsWizzard.class.getSimpleName();

    public GUILocationsWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.position_560);
        setTitleText("Geo-Positionen");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        JSONArray list = IOTDevice.list.getUUIDList();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevice.list.getEntry(uuid);

            if (device == null) continue;

            if (! device.hasCapability("fixed")) continue;

            boolean isnice = (device.fixedLatFine != null)
                    && (device.fixedLonFine != null)
                    && (device.fixedAltFine != null);

            if (todo && isnice) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(uuid);

            entry.setOnUpdateContentListener(onUpdateContentListener);
            entry.setOnClickListener(onClickListener);
        }
    }

    private final GUIListEntryIOT.OnUpdateContentListener onUpdateContentListener =
            new GUIListEntryIOT.OnUpdateContentListener()
    {
        @Override
        public void onUpdateContent(GUIListEntryIOT entry)
        {
            String info = "Keine Geo-Position";

            boolean isnice = false;

            IOTDevice device = IOTDevice.list.getEntry(entry.uuid);

            if (device != null)
            {
                isnice = (device.fixedLatFine != null)
                        && (device.fixedLonFine != null)
                        && (device.fixedAltFine != null);

                if (isnice)
                {
                    info = ""
                            + Simple.getRounded3(device.fixedLatFine)
                            + " "
                            + Simple.getRounded3(device.fixedLonFine)
                            + " "
                            + Simple.getRounded3(device.fixedAltFine)
                            + " m";
                }
            }

            entry.infoView.setText(info);

            entry.infoView.setTextColor(isnice
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);
        }
    };

    private final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTDevice device = IOTDevice.list.getEntry(((GUIListEntryIOT) view).uuid);
            GUI.instance.desktopActivity.displayWizzard(GUIGeomapWizzard.class.getSimpleName(), device);
        }
    };
}
