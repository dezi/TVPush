package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.plugin.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class GUILocationsWizzard extends GUIPluginTitleList
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

            IOTStatus status = IOTStatusses.getEntry(uuid);

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(uuid, device, status);

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

            if (entry.device != null)
            {
                isnice = (entry.device.fixedLatFine != null)
                        && (entry.device.fixedLonFine != null)
                        && (entry.device.fixedAltFine != null);

                if (isnice)
                {
                    info = ""
                            + Simple.getRounded3(entry.device.fixedLatFine)
                            + " "
                            + Simple.getRounded3(entry.device.fixedLonFine)
                            + " "
                            + Simple.getRounded3(entry.device.fixedAltFine)
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
            IOTDevice device = ((GUIListEntryIOT) view).device;
            GUI.instance.desktopActivity.displayWizzard(GUIGeomapWizzard.class.getSimpleName(), device);
        }
    };
}
