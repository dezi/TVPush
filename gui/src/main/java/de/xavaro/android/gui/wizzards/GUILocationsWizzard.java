package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.base.IOTObject;
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

            GUIListEntry entry = listView.findGUIListEntryIOTOrCreate(uuid, device, null);
            entry.setOnClickListener(onClickListener);
            entry.setIDTag(uuid);
            entry.setTag(device);

            String info = "Keine Geo-Position";

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

            entry.iconView.setImageResource(GUIIcons.getImageResid(device));
            entry.headerViev.setText(device.name);
            entry.infoView.setText(info);

            entry.infoView.setTextColor(isnice
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);
        }
    }

    private final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTObject iotobject = (IOTObject) view.getTag();

            GUI.instance.desktopActivity.displayWizzard(GUIGeomapWizzard.class.getSimpleName(), iotobject);
        }
    };
}
