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
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.iot.things.IOTLocation;
import de.xavaro.android.iot.things.IOTThing;

public class GUIGeoposWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUIGeoposWizzard.class.getSimpleName();

    public GUIGeoposWizzard(Context context)
    {
        super(context);

        setWizzard(true, false);

        setTitleIcon(R.drawable.position_560);
        setNameInfo("Geo-Positionen");
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

            Double lat = null;
            Double lon = null;
            Double alt = null;

            IOTThing iotThing = IOTThing.getEntry(entry.uuid);

            if (iotThing instanceof IOTDevice)
            {
                IOTDevice device = (IOTDevice) iotThing;

                lat = device.fixedLatFine;
                lon = device.fixedLonFine;
                alt = device.fixedAltFine;
           }

            if (iotThing instanceof IOTDomain)
            {
                IOTDomain domain = (IOTDomain) iotThing;

                lat = domain.fixedLatFine;
                lon = domain.fixedLonFine;
                alt = domain.fixedAltFine;
            }

            if (iotThing instanceof IOTLocation)
            {
                IOTLocation location = (IOTLocation) iotThing;

                lat = location.fixedLatFine;
                lon = location.fixedLonFine;
                alt = location.fixedAltFine;
            }

            isnice = (lat != null) && (lon != null) && (alt != null);

            if (isnice)
            {
                info = ""
                        + Simple.getRounded3(lat)
                        + " "
                        + Simple.getRounded3(lon)
                        + " "
                        + Simple.getRounded3(alt)
                        + " m";
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
            String uuid = ((GUIListEntryIOT) view).uuid;
            GUI.instance.desktopActivity.displayWizzard(GUIGeomapWizzard.class.getSimpleName(), uuid);
        }
    };
}
