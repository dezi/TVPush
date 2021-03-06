package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.iot.things.IOTDevice;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.plugin.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.simple.Json;

public class GUICamerasWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUICamerasWizzard.class.getSimpleName();

    public GUICamerasWizzard(Context context)
    {
        super(context);

        setWizzard(true, false);

        setTitleIcon(R.drawable.camera_shutter_820);
        setNameInfo("Cameras");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
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

            if (! device.type.equals("camera")) continue;

            if (todo) continue;

            GUIListEntryIOT entry = listView.findGUIListEntryIOTOrCreate(uuid);
            entry.setOnClickListener(onClickListener);
            entry.infoView.setVisibility(GONE);
        }
    }

    private final View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String uuid = ((GUIListEntryIOT) view).uuid;
            GUI.instance.desktopActivity.displayWizzard(GUICameraWizzard.class.getSimpleName(), uuid);
        }
    };
}
