package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.plugin.GUIPluginTitleList;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.R;

public class GUISettingsWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUISettingsWizzard.class.getSimpleName();

    public GUISettingsWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, true, 1, Gravity.END);

        setTitleIcon(R.drawable.wizzard_settings_200);
        setTitleText("Subsystem Settings");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        JSONObject subsystemInfo = GUI.instance.subSystems.getSubsystemInfos(objectTag);

        String subsystemName = Json.getString(subsystemInfo, "name");
        String subsystemIcon = Json.getString(subsystemInfo, "icon");
        setTitleIcon(subsystemIcon);
        setTitleText(subsystemName);

        JSONArray services = Json.getArray(subsystemInfo, "services");

        if (services != null)
        {
            for (int inx = 0; inx < services.length(); inx++)
            {
                JSONObject service = Json.getObject(services, inx);
                if (service == null) continue;

                String tag = Json.getString(service, "tag");
                String name = Json.getString(service, "name");

                GUIListEntry entry = listView.findGUIListEntryOrCreate(tag);
                entry.setOnClickListener(onSubServiceClickListener);
                entry.setTag(tag);

                entry.headerViev.setText(name);
            }
        }

        JSONArray features = Json.getArray(subsystemInfo, "features");

        if (features != null)
        {
            for (int inx = 0; inx < features.length(); inx++)
            {
                JSONObject feature = Json.getObject(features, inx);
                if (feature == null) continue;

                String tag = Json.getString(feature, "tag");
                String name = Json.getString(feature, "name");

                GUIListEntry entry = listView.findGUIListEntryOrCreate(tag);
                entry.setOnClickListener(onSubServiceClickListener);
                entry.setTag(tag);

                entry.headerViev.setText(name);
            }
        }
    }

    private final OnClickListener onSubServiceClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
        }
    };
}
