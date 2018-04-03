package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import org.json.JSONObject;

import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUISubSystems;
import de.xavaro.android.gui.plugin.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.views.GUIListView;
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

        String name = Json.getString(subsystemInfo, "name");
        setTitleInfo(name);
    }

    private final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
        }
    };
}
