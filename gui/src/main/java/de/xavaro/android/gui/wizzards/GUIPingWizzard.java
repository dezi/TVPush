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
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.base.IOTAlive;
import de.xavaro.android.iot.status.IOTCredential;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import pub.android.interfaces.drv.SmartPlug;

public class GUIPingWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUIPingWizzard.class.getSimpleName();

    public GUIPingWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, false);

        setTitleIcon(R.drawable.ping_440);
        setTitleText("Ping Wizzard");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public static void collectEntries(GUILinearLayout listView, boolean todo)
    {
        JSONArray list = IOTDevices.instance.getListUUIDs();

        for (int inx = 0; inx < list.length(); inx++)
        {
            String uuid = Json.getString(list, inx);
            IOTDevice device = IOTDevices.getEntry(uuid);

            if (device == null) continue;
            if (todo) continue;

            IOTStatus status = new IOTStatus(device.uuid);//IOTStatusses.getEntry(device.uuid);
            if (status == null) continue;

            String connect = (status.ipaddr != null) ? status.ipaddr : status.macaddr;
            if (connect == null) continue;

            GUIListEntryIOT entry = new GUIListEntryIOT(listView.getContext());

            entry.uuid = uuid;
            entry.device = device;
            entry.status = status;

            entry.updateContent();

            listView.addView(entry);
        }
    }
}
