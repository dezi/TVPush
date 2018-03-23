package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.status.IOTStatusses;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIPluginTitleListIOT;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntryIOT;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.simple.Json;

public class GUICamerasWizzard extends GUIPluginTitleListIOT
{
    private final static String LOGTAG = GUICamerasWizzard.class.getSimpleName();

    public GUICamerasWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.camera_shutter_820);
        setTitleText("Cameras");
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

            if (! device.type.equals("camera")) continue;

            if (todo) continue;

            IOTStatus status = IOTStatusses.getEntry(device.uuid);

            GUIListEntryIOT entry = new GUIListEntryIOT(listView.getContext());
            entry.setOnClickListener(onClickListener);

            entry.uuid = uuid;
            entry.device = device;
            entry.status = status;

            entry.updateContent();

            listView.addView(entry);
        }
    }

    private static final View.OnClickListener onClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTDevice device = ((GUIListEntryIOT) view).device;

            GUI.instance.desktopActivity.displayWizzard(GUICameraWizzard.class.getSimpleName(), device);
        }
    };
}
