package de.xavaro.android.gui.wizzards;

import android.content.Context;

import org.json.JSONArray;

import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.R;

import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;

public class GUIPingWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUIPingWizzard.class.getSimpleName();

    public GUIPingWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.ping_440);
        setNameText("Ping Wizzard");
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

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevice.list.getEntry(uuid);

            if (device == null) continue;
            if (todo) continue;

            IOTStatus status = IOTStatus.list.getEntry(uuid);
            if (status == null) continue;

            String connect = (status.ipaddr != null) ? status.ipaddr : status.macaddr;
            if (connect == null) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(uuid);

            entry.infoView.setText(connect);
        }
    }
}
