package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.iot.things.IOTLocation;

import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.R;

public class GUILocationsWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUILocationsWizzard.class.getSimpleName();

    public GUILocationsWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, true, 1, Gravity.CENTER);

        setTitleIcon(R.drawable.location_240);
        setTitleText("Örtlichkeiten");

        setActionIconVisible(R.drawable.add_540, true);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        setSize(1, Gravity.CENTER);
    }

    @Override
    public void onActionIconClicked()
    {
        Log.d(LOGTAG, "onActionIconClicked:");

        IOTLocation location = new IOTLocation();

        location.fixedwifi = Simple.getConnectedWifiName();
        location.name = "Neuer Ort";

        /*
        if (uuid != null)
        {
            IOTThing iotThing = IOTThing.getEntry(uuid);

            if (iotThing != null)
            {
                location.name += " in " + ((iotThing.nick != null) ? iotThing.nick : iotThing.name);
            }
        }
        */

        IOTLocation.list.addEntry(location, true, true);

        updateContent();
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        JSONArray list = IOTLocation.list.getUUIDList();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTLocation location = IOTLocation.list.getEntry(uuid);

            if (location == null) continue;

            boolean isnice = (location.fixedLatFine != null)
                    && (location.fixedLonFine != null)
                    && (location.fixedAltFine != null);

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

            IOTLocation location = IOTLocation.list.getEntry(entry.uuid);

            if (location != null)
            {
                isnice = (location.fixedLatFine != null)
                        && (location.fixedLonFine != null)
                        && (location.fixedAltFine != null);

                if (isnice)
                {
                    info = ""
                            + Simple.getRounded3(location.fixedLatFine)
                            + " "
                            + Simple.getRounded3(location.fixedLonFine)
                            + " "
                            + Simple.getRounded3(location.fixedAltFine)
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
        public void onClick(final View view)
        {
            stackLeft(new Runnable()
            {
                @Override
                public void run()
                {
                    GUI.instance.desktopActivity.displayWizzard(false, GUIDomainsWizzard.class.getSimpleName());

                    String uuid = ((GUIListEntryIOT) view).uuid;
                    GUI.instance.desktopActivity.displayWizzard(GUIGeomapWizzard.class.getSimpleName(), uuid);
                }
            });
        }
    };
}
