package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;

import de.xavaro.android.gui.plugin.GUIPluginTitleList;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.base.GUI;

import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.R;

public class GUISetupWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUISetupWizzard.class.getSimpleName();

    public GUISetupWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, 1);

        setTitleIcon(R.drawable.permissions_240);
        setTitleText("System Setup");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public void collectEntries(GUIListView listView, boolean todo)
    {
        collectSubsystems(listView, todo);
    }

    private void collectSubsystems(GUIListView listView, boolean todo)
    {
        JSONArray subsystems = GUISetup.getAvailableSubsystems();

        for (int inx = 0; inx < subsystems.length(); inx++)
        {
            JSONObject infos = Json.getObject(subsystems, inx);
            if (infos == null) continue;

            String subsystem = Json.getString(infos, "drv");
            String name = Json.getString(infos, "name");
            if ((subsystem == null) || (name == null)) continue;

            String type = Json.getString(infos, "type");
            if (type == null) type = SubSystemHandler.SUBSYSTEM_TYPE_SERVICE;

            String icon = Json.getString(infos, "icon");

            int mode = Json.getInt(infos, "mode");

            int state = GUISetup.getSubsystemState(subsystem);
            int runstate = GUISetup.getSubsystemRunState(subsystem);
            boolean enabled = (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);

            if (todo && enabled) continue;

            GUIListEntry entry = listView.findGUIListEntryOrCreate(subsystem);
            entry.setOnClickListener(onClickListener);
            entry.setTag(subsystem);

            entry.iconView.setImageResource(icon);
            entry.headerViev.setText(name);

            String info = GUISetup.getTextForSubsystemEnabled(name, state, mode);

            if (enabled && type.equals(SubSystemHandler.SUBSYSTEM_TYPE_SERVICE))
            {
                info += " - " + Simple.getTrans(GUISetup.getTextForSubsystemRunstateResid(runstate));
            }

            entry.infoView.setText(info);

            int color = ((runstate == SubSystemHandler.SUBSYSTEM_RUN_STARTED)
                    || (mode == SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE))
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
                    ? GUIDefs.TEXT_COLOR_ALERTS
                    : GUIDefs.TEXT_COLOR_SPECIAL;

            entry.infoView.setTextColor(color);
        }
    }

    private final OnClickListener onClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String subsystem = (String) view.getTag();
            GUI.instance.desktopActivity.displayWizzard(GUISettingsWizzard.class.getSimpleName(), subsystem);
        }
    };
}