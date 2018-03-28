package de.xavaro.android.gui.wizzards;

import android.content.Context;

import org.json.JSONArray;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.simple.Json;

import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class GUIPingWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUIPingWizzard.class.getSimpleName();

    public GUIPingWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.ping_440);
        setTitleText("Ping Wizzard");
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
            if (todo) continue;

            IOTStatus status = IOTStatusses.getEntry(uuid);
            if (status == null) continue;

            String connect = (status.ipaddr != null) ? status.ipaddr : status.macaddr;
            if (connect == null) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(uuid, device, status);

            entry.infoView.setText(connect);
        }
    }
}
