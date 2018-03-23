package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;

import java.util.Map;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUIIcons;
import de.xavaro.android.gui.base.GUIPlugin;
import de.xavaro.android.gui.base.GUIPluginTitle;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.iot.things.IOTDevice;
import de.xavaro.android.iot.things.IOTDevices;

import static de.xavaro.android.gui.base.GUI.instance;

public class GUIMenuWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUIMenuWizzard.class.getSimpleName();

    public GUIMenuWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, false);

        setTitleIcon(R.drawable.menu_400);
        setTitleText("Menu");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public static void collectEntries(GUILinearLayout listView, boolean todo)
    {
        Map<String,GUIPlugin> wizzards = GUI.instance.desktopActivity.getWizzards();

        for (Map.Entry<String, GUIPlugin> mapentry : wizzards.entrySet())
        {
            String name = mapentry.getKey();

            if (name.equals(GUIMenuWizzard.class.getSimpleName())) continue;

            GUIPlugin wizzard = mapentry.getValue();

            if (! (wizzard instanceof GUIPluginTitle)) continue;
            if (wizzard.isHelper()) continue;

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onClickListener);
            entry.setTag(name);

            entry.iconView.setImageResource(((GUIPluginTitle) wizzard).getTitleIconResid());
            entry.headerViev.setText(((GUIPluginTitle) wizzard).getTitleText());

            listView.addView(entry);
        }
    }

    private static final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String name = (String) view.getTag();

            GUI.instance.desktopActivity.displayWizzard(name);
        }
    };
}
