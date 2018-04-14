package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.util.Log;

import org.json.JSONArray;

import de.xavaro.android.iot.things.IOTLocation;
import de.xavaro.android.iot.things.IOTDomain;

import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.base.GUIShort;
import de.xavaro.android.gui.base.GUIUtil;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.R;

public class GUILocationsWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUILocationsWizzard.class.getSimpleName();

    private Class lastHelper;

    public String domuuid;

    public GUILocationsWizzard(Context context)
    {
        super(context);

        setWizzard(true, true);
        setSize(1, Gravity.CENTER);

        setTitleIcon(R.drawable.location_240);
        setNameText("Orte");

        setActionIconVisible(R.drawable.add_540, true);
    }

    public void setDomain(String domuuid)
    {
        this.domuuid = domuuid;

        IOTDomain domain = IOTDomain.list.getEntry(domuuid);

        if (domain == null)
        {
            setNameText("Orte");
        }
        else
        {
            setNameText("Orte in " + ((domain.nick != null) ? domain.nick : domain.name));
        }

        listView.removeAllViews();

        onCollectEntries(listView, false);
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        setSize(1, Gravity.CENTER);

        lastHelper = null;
    }

    @Override
    public void onActionIconClicked()
    {
        Log.d(LOGTAG, "onActionIconClicked:");

        IOTLocation location = new IOTLocation();

        location.fixedwifi = Simple.getConnectedWifiName();
        location.name = "Neuer Ort";

        IOTDomain domain = IOTDomain.list.getEntry(domuuid);

        if (domain != null)
        {
            location.name += " in " + ((domain.nick != null) ? domain.nick : domain.name);
        }

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

            if (GUIShort.isWizzardPresent(GUIFixedWizzard.class))
            {
                GUIFixedWizzard fixed = (GUIFixedWizzard) GUIShort.getWizzard(GUIFixedWizzard.class);
                if (fixed != null) fixed.setLocation(entry.uuid);
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

        //
        // Attach locations w/o positions first.
        //

        for (int inx = 0; inx < list.length(); inx++)
        {
            String locuuid = Json.getString(list, inx);
            IOTLocation location = IOTLocation.list.getEntry(locuuid);

            if (location == null) continue;

            boolean isnice = (location.fixedLatFine != null)
                    && (location.fixedLonFine != null)
                    && (location.fixedAltFine != null);

            if (todo || isnice) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(locuuid);

            entry.setOnUpdateContentListener(onUpdateContentListener);
            entry.setOnClickListener(onClickListener);
        }

        //
        // Attach locations nearest to this domain.
        //

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
            String uuid = ((GUIListEntryIOT) view).uuid;

            IOTLocation location = IOTLocation.list.getEntry(uuid);
            if (location == null) return;

            boolean isnice = (location.fixedLatFine != null)
                    && (location.fixedLonFine != null)
                    && (location.fixedAltFine != null);

            if (! isnice)
            {
                showGeomapWizzard(uuid);
            }
            else
            {
                if ((lastHelper == null) || (lastHelper == GUIGeomapWizzard.class))
                {
                    showFixedWizzard(uuid);
                }
                else
                {
                    if (lastHelper == GUIFixedWizzard.class)
                    {
                        showGeomapWizzard(uuid);
                    }
                }
            }
        }
    };

    private void showFixedWizzard(final String uuid)
    {
        GUIShort.hideWizzard(GUIGeomapWizzard.class);

        stackCenter(new Runnable()
        {
            @Override
            public void run()
            {
                GUIShort.showWizzard(GUIDomainsWizzard.class);

                lastHelper = GUIFixedWizzard.class;
                GUIShort.showWizzard(lastHelper, uuid);

                GUIFixedWizzard fixed = (GUIFixedWizzard) GUIShort.getWizzard(GUIFixedWizzard.class);
                if (fixed != null) fixed.setLocation(uuid);
            }
        });
    }

    private void showGeomapWizzard(final String uuid)
    {
        if (! GUIShort.isWizzardPresent(GUIGeomapWizzard.class))
        {
            stackStart(new Runnable()
            {
                @Override
                public void run()
                {
                    GUIShort.hideWizzard(GUIFixedWizzard.class);
                    GUIShort.hideWizzard(GUIDomainsWizzard.class);

                    lastHelper = GUIGeomapWizzard.class;
                    GUIShort.showWizzard(lastHelper, uuid);

                    IOTLocation location = IOTLocation.list.getEntry(uuid);
                    if (location == null) return;

                    if ((location.fixedLatFine != null)
                            && (location.fixedLonFine != null)
                            && (location.fixedAltFine != null))
                    {
                        //
                        // All set, nothing to do.
                        //

                        return;
                    }

                    IOTDomain domain = IOTDomain.list.getEntry(domuuid);
                    if (domain == null) return;

                    if ((domain.fixedLatFine == null)
                            || (domain.fixedLonFine == null)
                            || (domain.fixedAltFine == null))
                    {
                        //
                        // Nothing can be done.
                        //

                        return;
                    }

                    //
                    // Preset geomap wizzard with domain location.
                    //

                    GUIGeomapWizzard geomap = (GUIGeomapWizzard) GUIShort.getWizzard(GUIGeomapWizzard.class);

                    if (geomap != null)
                    {
                        geomap.setCoordinates(domain.fixedLatFine, domain.fixedLonFine, domain.fixedAltFine);
                    }
                }
            });
        }
    }
}
