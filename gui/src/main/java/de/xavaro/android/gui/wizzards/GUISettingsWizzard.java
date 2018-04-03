package de.xavaro.android.gui.wizzards;

import android.content.Context;
import android.view.Gravity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import pub.android.interfaces.all.SubSystemHandler;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.plugin.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Simple;
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
        if (subsystemInfo == null) return;

        String subsystem = Json.getString(subsystemInfo, "drv");
        String subsystemName = Json.getString(subsystemInfo, "name");
        String subsystemIcon = Json.getString(subsystemInfo, "icon");
        setTitleIcon(subsystemIcon);
        setTitleText(subsystemName);

        JSONArray settings = Json.getArray(subsystemInfo, "settings");

        if (settings != null)
        {
            for (int inx = 0; inx < settings.length(); inx++)
            {
                JSONObject setting = Json.getObject(settings, inx);
                if (setting == null) continue;

                String tag = Json.getString(setting, "tag");
                String type = Json.getString(setting, "type");
                String name = Json.getString(setting, "name");
                String icon = Json.getString(setting, "icon");
                if ((tag == null) || (type == null) || (name == null)) continue;

                String subtag = subsystem + "." + tag;
                int state = GUISetup.getSubsystemState(subtag);
                int runstate = GUISetup.getSubsystemRunState(subtag);

                GUIListEntry entry = listView.findGUIListEntryOrCreate(subtag);
                entry.setOnClickListener(onSubServiceSettingClickListener);
                entry.setTag(subtag);

                entry.iconView.setImageResource(icon);
                entry.headerViev.setText(name);

                String info = GUISetup.getTextForSubsystemEnabled(name, state);

                if (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
                {
                    if (type.equals(SubSystemHandler.SUBSYSTEM_TYPE_SERVICE))
                    {
                        info += " - " + Simple.getTrans(GUISetup.getTextForSubsystemRunstateResid(runstate));
                    }
                }

                entry.infoView.setText(info);

                int color = (state == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
                        ? GUIDefs.TEXT_COLOR_SPECIAL
                        : (runstate == SubSystemHandler.SUBSYSTEM_RUN_STARTED)
                        ? GUIDefs.TEXT_COLOR_INFOS
                        : GUIDefs.TEXT_COLOR_ALERTS;

                entry.infoView.setTextColor(color);

                String need = Json.getString(setting, "need");
                collectSubsystemsNeeds(listView, subtag, need, todo);
            }
        }
   }

    private void collectSubsystemsNeeds(GUIListView listView, String subtag, String needString, boolean todo)
    {
        if (needString == null) return;

        String[] needs = needString.split("\\+");

        for (String need : needs)
        {
            int icon = GUISetup.getIconForNeedResid(need);
            int text = GUISetup.getTextForNeedResid(need);

            GUIListEntry entry = listView.findGUIListEntryOrCreate(subtag + "." + need);
            entry.setOnClickListener(onSubServiceSettingClickListener);
            entry.setTag(subtag);

            entry.iconView.setImageResource(icon);
            entry.headerViev.setText(text);
            entry.setLevel(1);

            JSONArray perms = GUISetup.getPermissionsForNeed(need);

            String infos = "";

            if (perms.length() > 0)
            {
                for (int inx = 0; inx < perms.length(); inx++)
                {
                    String perm = Json.getString(perms, inx);
                    if (perm == null) continue;

                    if (infos.length() > 0) infos += ", ";

                    infos += Simple.getTrans(GUISetup.getTextForPermissionResid(perm));
                }
            }

            entry.infoView.setText(infos);
        }
    }

    private final OnClickListener onSubServiceSettingClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
        }
    };
}
