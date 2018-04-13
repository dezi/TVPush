package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.base.GUIShort;
import de.xavaro.android.gui.base.GUIUtil;
import de.xavaro.android.iot.things.IOTLocation;

import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.R;

public class GUILocationsWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUILocationsWizzard.class.getSimpleName();

    private String lastHelper;

    public String domuuid;

    public GUILocationsWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, true, 1, Gravity.CENTER);

        setTitleIcon(R.drawable.location_240);
        setNameText("Örtlichkeiten");

        setActionIconVisible(R.drawable.add_540, true);
    }

    public void setDomain(String domuuid)
    {
        this.domuuid = domuuid;

        listView.removeAllViews();

        onCollectEntries(listView, false);
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
    public void onSelectionChanged(GUIListEntryIOT entry, boolean selected)
    {
        android.util.Log.d(LOGTAG, "onSelectionChanged: entry=" + entry.uuid  + " selected=" + selected);
        if (selected)
        {
            if (GUIShort.isWizzardPresent(GUIGeomapWizzard.class))
            {
                GUIGeomapWizzard geomap = (GUIGeomapWizzard) GUIShort.getWizzard(GUIGeomapWizzard.class);
                if (geomap != null) geomap.setIOTObject(entry.uuid);
            }
        }
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        if (domuuid != null) collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        JSONArray list = IOTLocation.list.getUUIDList();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String locuuid = Json.getString(list, inx);
            IOTLocation location = IOTLocation.list.getEntry(locuuid);

            if (location == null) continue;

            boolean isnice = (location.fixedLatFine != null)
                    && (location.fixedLonFine != null)
                    && (location.fixedAltFine != null);

            if (todo && isnice) continue;
            if (! isnice) continue;

            String domuuid = GUIUtil.getClosestDomainForLocation(
                    location.fixedLatFine,
                    location.fixedLonFine,
                    location.fixedAltFine);

            if ((domuuid == null) || ! domuuid.equals(this.domuuid)) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(locuuid);

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
            IOTLocation location = IOTLocation.list.getEntry(entry.uuid);
            if (location == null) return;

            if ((location.fixedLatFine == null)
                    || (location.fixedLonFine == null)
                    || (location.fixedAltFine == null))
            {
                entry.infoView.setText("Keine Geo-Position");
                entry.infoView.setTextColor(GUIDefs.TEXT_COLOR_ALERTS);
            }
            else
            {
                entry.infoView.setTextColor(GUIDefs.TEXT_COLOR_INFOS);
            }
        }
    };

    private final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            final String uuid = ((GUIListEntryIOT) view).uuid;

            if ((lastHelper == null) || lastHelper.equals(GUIGeomapWizzard.class.getSimpleName()))
            {
                GUI.instance.desktopActivity.displayWizzard(false, lastHelper);
                lastHelper = GUIFixedWizzard.class.getSimpleName();

                stackCenter(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        GUI.instance.desktopActivity.displayWizzard(true, GUIDomainsWizzard.class.getSimpleName());
                        GUI.instance.desktopActivity.displayWizzard(lastHelper, uuid);
                    }
                });
            }
            else
            {
                GUI.instance.desktopActivity.displayWizzard(false, lastHelper);
                lastHelper = GUIGeomapWizzard.class.getSimpleName();

                stackEnd(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        GUI.instance.desktopActivity.displayWizzard(false, GUIDomainsWizzard.class.getSimpleName());
                        GUI.instance.desktopActivity.displayWizzard(lastHelper, uuid);
                    }
                });
            }
        }
    };
}
