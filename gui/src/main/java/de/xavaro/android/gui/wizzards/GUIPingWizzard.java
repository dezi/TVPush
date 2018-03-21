package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Map;

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

    private Thread pinger;

    public GUIPingWizzard(Context context)
    {
        super(context);

        setTitleIcon(R.drawable.beacon_220);
        setTitleText("Ping Wizzard");
    }

    @Override
    public void onAttachedToWindow()
    {
        super.onAttachedToWindow();

        if (pinger == null)
        {
            pinger = new Thread(pingerRunner);
            pinger.start();
        }
    }

    @Override
    public void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();

        if (pinger != null)
        {
            pinger.interrupt();
            pinger = null;
        }
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

            Boolean pingt = Simple.getMapBoolean(pingerStatusse, status.uuid);

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onClickListener);
            entry.setTag(status);

            entry.iconView.setImageResource(GUIIcons.getImageResid(device));
            entry.headerViev.setText(device.name);
            entry.infoView.setText(status.ipaddr);

            if (pingt != null)
            {
                entry.iconView.setBackgroundColor(pingt ? 0x88008800 : 0x88880000);
            }

            listView.addView(entry);
        }
    }

    private static final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {

        }
    };

    private static Map<String, Boolean> pingerStatusse = new HashMap<>();

    private void pingerStatus(String uuid, int inx, boolean pingt)
    {
        pingerStatusse.put(uuid, pingt);

        GUIListEntry entry = (GUIListEntry) listView.getChildAt(inx);
        entry.iconView.setBackgroundColor(pingt ? 0x88008800 : 0x88880000);
    }

    private final Runnable pingerRunner = new Runnable()
    {
        @Override
        public void run()
        {
            while (pinger != null)
            {
                for (int inx = 0; inx < listView.getChildCount(); inx++)
                {
                    View child = listView.getChildAt(inx);
                    if (child == null) continue;

                    IOTStatus status = (IOTStatus) child.getTag();
                    if (status == null) continue;

                    boolean reachable = false;

                    try
                    {
                        Process p1 = Runtime.getRuntime().exec("ping -c 1 -W 1 " + status.ipaddr);
                        reachable = (p1.waitFor() == 0);
                    }
                    catch (Exception ignore)
                    {
                    }

                    final int cbinx = inx;
                    final String cbuuid = status.uuid;
                    final boolean cbpingt = reachable;

                    Simple.getHandler().post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            pingerStatus(cbuuid, cbinx, cbpingt);
                        }
                    });
                }

                Simple.sleep(2 * 1000);
            }
        }
    };
}
