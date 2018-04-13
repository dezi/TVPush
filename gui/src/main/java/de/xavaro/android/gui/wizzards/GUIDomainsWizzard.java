package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.things.IOTDomain;
import de.xavaro.android.gui.base.GUIShort;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.R;

public class GUIDomainsWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUIDomainsWizzard.class.getSimpleName();

    private Class lastHelper;

    public GUIDomainsWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.domains_540);
        setNameText("Domänen");

        setActionIconVisible(R.drawable.add_540, true);
    }

    @Override
    public void onSelectionChanged(GUIListEntryIOT entry, boolean selected)
    {
        android.util.Log.d(LOGTAG, "onSelectionChanged: entry=" + entry.uuid + " selected=" + selected);

        if (selected)
        {
            if (GUIShort.isWizzardPresent(GUIGeomapWizzard.class))
            {
                GUIGeomapWizzard geomap = (GUIGeomapWizzard) GUIShort.getWizzard(GUIGeomapWizzard.class);
                if (geomap != null) geomap.setIOTObject(entry.uuid);
            }

            if (GUIShort.isWizzardPresent(GUILocationsWizzard.class))
            {
                GUILocationsWizzard location = (GUILocationsWizzard) GUIShort.getWizzard(GUILocationsWizzard.class);
                if (location != null) location.setDomain(entry.uuid);
            }
        }
    }

    @Override
    public void onActionIconClicked()
    {
        Log.d(LOGTAG, "onActionIconClicked:");

        IOTDomain domain = new IOTDomain();

        domain.fixedwifi = Simple.getConnectedWifiName();
        domain.name = domain.fixedwifi;

        IOTDomain.list.addEntry(domain, true, true);

        updateContent();
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        JSONArray list = IOTDomain.list.getUUIDList();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDomain domain = IOTDomain.list.getEntry(uuid);

            if (domain == null) continue;

            boolean isnice = (domain.fixedLatFine != null)
                    && (domain.fixedLonFine != null)
                    && (domain.fixedAltFine != null);

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
            IOTDomain domain = IOTDomain.list.getEntry(entry.uuid);
            if (domain == null) return;

            if ((domain.fixedLatFine == null)
                    || (domain.fixedLonFine == null)
                    || (domain.fixedAltFine == null))
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
            String uuid = ((GUIListEntryIOT) view).uuid;

            IOTDomain domain = IOTDomain.list.getEntry(uuid);
            if (domain == null) return;

            boolean isnice = (domain.fixedLatFine != null)
                    && (domain.fixedLonFine != null)
                    && (domain.fixedAltFine != null);

            if (!isnice)
            {
                showGeomapWizzard(uuid);
            }
            else
            {
                if ((lastHelper == null) || (lastHelper == GUIGeomapWizzard.class))
                {
                    showLocationWizzard(uuid);
                }
                else
                {
                    if (lastHelper == GUILocationsWizzard.class)
                    {
                        showGeomapWizzard(uuid);
                    }
                }
            }
        }
    };

    private void showLocationWizzard(String uuid)
    {
        GUIShort.hideWizzard(GUIGeomapWizzard.class);

        lastHelper = GUILocationsWizzard.class;
        GUIShort.showWizzard(lastHelper, uuid);
    }

    private void showGeomapWizzard(String uuid)
    {
        if (!GUIShort.isWizzardPresent(GUIGeomapWizzard.class))
        {
            GUIShort.hideWizzard(GUIFixedWizzard.class);
            GUIShort.hideWizzard(GUIGeomapWizzard.class);

            lastHelper = GUIGeomapWizzard.class;
            GUIShort.showWizzard(lastHelper, uuid);
        }
    }
}
