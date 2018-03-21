package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import java.net.InetAddress;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.status.IOTStatus;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

public class GUIPingWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUIPingWizzard.class.getSimpleName();

    public GUIPingWizzard(Context context)
    {
        super(context);

        setTitleIcon(R.drawable.beacon_220);
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

            IOTStatus status = new IOTStatus(device.uuid);
            if (status.ipaddr == null) continue;

            InetAddress inetAddress = Simple.getInetAddress(status.ipaddr);
            if (inetAddress == null) continue;

            boolean pingt = Simple.getInetPing(inetAddress,500);

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onClickListener);
            entry.setTag(device);

            entry.iconView.setImageResource(GUIIcons.getImageResid(device));
            entry.headerViev.setText(device.name);
            entry.infoView.setText(status.ipaddr);

            entry.infoView.setBackgroundColor(pingt ? 0x88008800 : 0x88880000);

            listView.addView(entry);
        }
    }

    private static final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            IOTDevice device = (IOTDevice) view.getTag();

        }
    };
}
