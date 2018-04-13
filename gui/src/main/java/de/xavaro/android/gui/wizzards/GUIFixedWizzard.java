package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;

import org.json.JSONArray;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Log;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;

public class GUIFixedWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUIFixedWizzard.class.getSimpleName();

    public GUIFixedWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, true, 1, Gravity.END);

        setTitleIcon(R.drawable.things_600);
        setNameText("Geräte");
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

            if (! device.hasCapability("fixed")) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(uuid);

            //entry.infoView.setText(connect);
        }
    }
}
