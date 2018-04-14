package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTLocation;

import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIShort;
import de.xavaro.android.gui.base.GUIUtil;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.R;

public class GUIFixedWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUIFixedWizzard.class.getSimpleName();

    private Class lastHelper;

    public String locuuid;

    public GUIFixedWizzard(Context context)
    {
        super(context);

        setWizzard(true, true);
        setSize(1, Gravity.END);

        setTitleIcon(R.drawable.things_600);
        setNameInfo("Geräte");
    }

    public void setLocation(String locuuid)
    {
        this.locuuid = locuuid;

        IOTLocation location = IOTLocation.list.getEntry(locuuid);

        if (location == null)
        {
            setNameInfo("Geräte");
        }
        else
        {
            setNameInfo("Geräte in " + ((location.nick != null) ? location.nick : location.name));
        }

        listView.removeAllViews();

        onCollectEntries(listView, false);
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
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        setSize(1, Gravity.END);

        lastHelper = null;
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        Log.d(LOGTAG, "onCollectEntries: todo=" + todo);

        collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        JSONArray list = IOTDevice.list.getUUIDList();

        //
        // Attach devices w/o positions first.
        //

        for (int inx = 0; inx < list.length(); inx++)
        {
            String fixuuid = Json.getString(list, inx);
            IOTDevice device = IOTDevice.list.getEntry(fixuuid);

            if ((device == null) || !device.hasCapability("fixed")) continue;

            boolean isnice = (device.fixedLatFine != null)
                    && (device.fixedLonFine != null)
                    && (device.fixedAltFine != null);

            if (todo || isnice) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(fixuuid);

            entry.setOnUpdateContentListener(onUpdateContentListener);
            entry.setOnClickListener(onClickListener);
        }

        //
        // Attach devices nearest to this location.
        //

        for (int inx = 0; inx < list.length(); inx++)
        {
            String fixuuid = Json.getString(list, inx);
            IOTDevice device = IOTDevice.list.getEntry(fixuuid);

            if ((device == null) || !device.hasCapability("fixed")) continue;

            boolean isnice = (device.fixedLatFine != null)
                    && (device.fixedLonFine != null)
                    && (device.fixedAltFine != null);

            if (todo || !isnice) continue;

            String locuuid = GUIUtil.getClosestLocationForDevice(
                    device.fixedLatFine,
                    device.fixedLonFine,
                    device.fixedAltFine);

            if ((locuuid == null) || !locuuid.equals(this.locuuid)) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(fixuuid);

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
                    IOTDevice fixed = IOTDevice.list.getEntry(entry.uuid);
                    if (fixed == null) return;

                    if ((fixed.fixedLatFine == null)
                            || (fixed.fixedLonFine == null)
                            || (fixed.fixedAltFine == null))
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

            IOTDevice fixed = IOTDevice.list.getEntry(uuid);
            if (fixed == null) return;

            boolean isnice = (fixed.fixedLatFine != null)
                    && (fixed.fixedLonFine != null)
                    && (fixed.fixedAltFine != null);

            if (! isnice)
            {
                showGeomapWizzard(uuid);
            }
            else
            {
                showGeomapWizzard(uuid);
            }
        }
    };

    private void showGeomapWizzard(final String uuid)
    {
        if (! GUIShort.isWizzardPresent(GUIGeomapWizzard.class))
        {
            stackStart(new Runnable()
            {
                @Override
                public void run()
                {
                    GUIShort.hideWizzard(GUILocationsWizzard.class);
                    GUIShort.hideWizzard(GUIDomainsWizzard.class);

                    lastHelper = GUIGeomapWizzard.class;
                    GUIShort.showWizzard(lastHelper, uuid);

                    IOTDevice fixed = IOTDevice.list.getEntry(uuid);
                    if (fixed == null) return;

                    if ((fixed.fixedLatFine != null)
                            && (fixed.fixedLonFine != null)
                            && (fixed.fixedAltFine != null))
                    {
                        //
                        // All set, nothing to do.
                        //

                        return;
                    }

                    IOTLocation location = IOTLocation.list.getEntry(locuuid);
                    if (location == null) return;

                    if ((location.fixedLatFine == null)
                            || (location.fixedLonFine == null)
                            || (location.fixedAltFine == null))
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
                        geomap.setCoordinates(location.fixedLatFine, location.fixedLonFine, location.fixedAltFine);
                    }
                }
            });
        }
    }
}

