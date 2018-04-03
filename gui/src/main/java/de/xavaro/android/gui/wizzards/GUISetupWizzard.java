package de.xavaro.android.gui.wizzards;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.plugin.GUIPluginTitleList;
import de.xavaro.android.gui.views.GUIDialogView;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import pub.android.interfaces.all.SubSystemHandler;

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
            JSONObject subsystemInfo = Json.getObject(subsystems, inx);
            if (subsystemInfo == null) continue;

            String subsystem = Json.getString(subsystemInfo, "drv");
            String name = Json.getString(subsystemInfo, "name");
            String icon = Json.getString(subsystemInfo, "icon");

            int state = GUISetup.getSubsystemState(subsystem);
            int runstate = GUISetup.getSubsystemRunState(subsystem);

            if (todo && (state != SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)) continue;

            GUIListEntry entry = listView.findGUIListEntryOrCreate(subsystem);
            entry.setOnClickListener(onClickListener);
            entry.setTag(subsystem);

            entry.iconView.setImageResource(icon);
            entry.headerViev.setText(name);

            String info = GUISetup.getTextForSubsystemEnabled(name, state);

            if (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
            {
                info += " - " + Simple.getTrans(GUISetup.getTextForSubsystemRunstateResid(runstate));
            }

            entry.infoView.setText(info);

            int color = (state == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
                    ? GUIDefs.TEXT_COLOR_SPECIAL
                    : (runstate == SubSystemHandler.SUBSYSTEM_RUN_STARTED)
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS;

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