package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;
import pub.android.interfaces.drv.Camera;

public class GUICamerasWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUICamerasWizzard.class.getSimpleName();

    public GUICamerasWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, 1);

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

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onClickListener);
            entry.setTag(device);

            entry.iconView.setImageResource(GUIIcons.getImageResid(device));
            entry.headerViev.setText(device.name);
            entry.infoView.setText(device.did);

            listView.addView(entry);
        }
    }

    private static final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTDevice device = (IOTDevice) view.getTag();

            Camera camera = GUI.instance.onRequestCameraByUUID(device.uuid);
            if (camera == null) return;

            //GUI.instance.desktopActivity.displayCamera(true, iotobject.uuid);
        }
    };
}
