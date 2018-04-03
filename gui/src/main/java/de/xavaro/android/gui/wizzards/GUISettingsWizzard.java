package de.xavaro.android.gui.wizzards;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.views.GUIDialogView;
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
        String subsystemNeed = Json.getString(subsystemInfo, "need");
        if ((subsystem == null) || (subsystemName == null)) return;

        int subsystemState = GUISetup.getSubsystemState(subsystem);
        int subsystemRunstate = GUISetup.getSubsystemRunState(subsystem);

        String subsystemInfoText = GUISetup.getTextForSubsystemEnabled(subsystemName, subsystemState);

        if (subsystemState == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
        {
            subsystemInfoText += " - " + Simple.getTrans(GUISetup.getTextForSubsystemRunstateResid(subsystemRunstate));
        }

        GUIListEntry subsystemEntry = listView.findGUIListEntryOrCreate(subsystem);
        subsystemEntry.setOnClickListener(onSubsystemClickListener);
        subsystemEntry.setTag(subsystemInfo);

        subsystemEntry.iconView.setImageResource(subsystemIcon);
        subsystemEntry.headerViev.setText(subsystemName);
        subsystemEntry.infoView.setText(subsystemInfoText);

        collectSubsystemsNeeds(listView, subsystem, subsystemNeed, todo);

        //
        // System specific settings.
        //

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

                String info = GUISetup.getTextForSubsystemEnabled(name, state);

                if (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
                {
                    if (type.equals(SubSystemHandler.SUBSYSTEM_TYPE_SERVICE))
                    {
                        info += " - " + Simple.getTrans(GUISetup.getTextForSubsystemRunstateResid(runstate));
                    }
                }

                int color = (state == SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED)
                        ? GUIDefs.TEXT_COLOR_SPECIAL
                        : (runstate == SubSystemHandler.SUBSYSTEM_RUN_STARTED)
                        ? GUIDefs.TEXT_COLOR_INFOS
                        : GUIDefs.TEXT_COLOR_ALERTS;

                GUIListEntry entry = listView.findGUIListEntryOrCreate(subtag);
                entry.setOnClickListener(onNeedClickListener);
                entry.setTag(subtag);

                entry.iconView.setImageResource(icon);
                entry.headerViev.setText(name);
                entry.infoView.setText(info);

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
            boolean needEnabled = GUISetup.haveNeed(need);

            int needIcon = GUISetup.getIconForNeedResid(need);
            int needText = GUISetup.getTextForNeedResid(need);

            GUIListEntry needEntry = listView.findGUIListEntryOrCreate(subtag + "." + need);
            needEntry.setOnClickListener(onNeedClickListener);
            needEntry.setTag(need);

            needEntry.setLevel(1);
            needEntry.iconView.setImageResource(needIcon);
            needEntry.headerViev.setText(needText);

            needEntry.infoView.setText(GUISetup.getTextForNeedStatusResid(need, needEnabled));

            needEntry.infoView.setTextColor(needEnabled
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);

            JSONArray needPerms = GUISetup.getPermissionsForNeed(need);

            if (needPerms.length() > 0)
            {
                int permIcon = GUISetup.getIconForPermResid(need);
                int permText = GUISetup.getTextForPermResid(need);

                String infos = "";

                for (int inx = 0; inx < needPerms.length(); inx++)
                {
                    String perm = Json.getString(needPerms, inx);
                    if (perm == null) continue;

                    if (infos.length() > 0) infos += ", ";

                    infos += Simple.getTrans(GUISetup.getTextForManifestPermResid(perm));
                }

                GUIListEntry permEntry = listView.findGUIListEntryOrCreate(subtag + "." + need + ".perm");
                permEntry.setOnClickListener(onPermClickListener);
                permEntry.setTag(need);

                permEntry.setLevel(1);
                permEntry.iconView.setImageResource(permIcon);
                permEntry.headerViev.setText(permText);

                permEntry.infoView.setText(infos);

                permEntry.infoView.setTextColor(GUISetup.haveAllPermissionsForNeed(getContext(), need)
                        ? GUIDefs.TEXT_COLOR_INFOS
                        : GUIDefs.TEXT_COLOR_ALERTS);

            }
        }
    }
    private final OnClickListener onSubsystemClickListener = new OnClickListener()
    {
        @Override
        public void onClick(final View entry)
        {
            JSONObject subsystemInfo = (JSONObject) entry.getTag();
            final String subsystem = Json.getString(subsystemInfo, "drv");
            if (subsystem == null) return;

            GUIDialogView dialog = new GUIDialogView(entry.getContext());

            dialog.setTitleText(Json.getString(subsystemInfo, "name"));
            dialog.setInfoText(Json.getString(subsystemInfo, "info"));

            if (Json.getInt(subsystemInfo, "mode") == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
            {
                dialog.setPositiveButton(R.string.basic_ok);
                dialog.positiveButton.requestFocus();
            }
            else
            {
                if (GUISetup.getSubsystemState(subsystem) == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
                {
                    dialog.setPositiveButton(R.string.basic_deactiviate, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUI.instance.subSystems.setSubsystemState(subsystem, SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED);
                            GUIPluginTitleList.updateContentinParentPlugin(entry);
                            GUI.instance.onStopSubsystemRequest(subsystem);
                        }
                    });

                    dialog.setNegativeButton(R.string.basic_cancel);

                    dialog.negativeButton.requestFocus();
                }
                else
                {
                    dialog.setPositiveButton(R.string.basic_activiate, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUI.instance.subSystems.setSubsystemState(subsystem, SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);
                            GUIPluginTitleList.updateContentinParentPlugin(entry);
                            GUI.instance.onStartSubsystemRequest(subsystem);
                        }
                    });

                    dialog.setNegativeButton(R.string.basic_postpone, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUI.instance.subSystems.setSubsystemState(subsystem, SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED);
                            GUIPluginTitleList.updateContentinParentPlugin(entry);
                        }
                    });

                    dialog.positiveButton.requestFocus();
                }
            }

            GUI.instance.desktopActivity.topframe.addView(dialog);
        }
    };

    private final OnClickListener onNeedClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            final String need = (String) view.getTag();

            GUIDialogView dialog = new GUIDialogView(getContext());

            dialog.setTitleText(GUISetup.getTextForNeedResid(need));
            dialog.setInfoText(GUISetup.getInfoForNeedResid(need));

            if (! GUISetup.needHasService(need))
            {
                dialog.setPositiveButton(R.string.basic_ok, null);
                dialog.positiveButton.requestFocus();
            }
            else
            {
                if (GUISetup.haveNeed(need))
                {
                    dialog.setPositiveButton(R.string.basic_deactiviate, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUISetup.startIntentForNeed(getContext(), need);
                        }
                    });

                    dialog.setNegativeButton(R.string.basic_cancel);

                    dialog.negativeButton.requestFocus();
                }
                else
                {
                    dialog.setPositiveButton(R.string.basic_activiate, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUISetup.startIntentForNeed(getContext(), need);
                        }
                    });

                    dialog.setNegativeButton(R.string.basic_postpone);

                    dialog.positiveButton.requestFocus();
                }
            }

            GUI.instance.desktopActivity.topframe.addView(dialog);
        }
    };

    private final OnClickListener onPermClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            final String need = (String) view.getTag();

            GUIDialogView dialog = new GUIDialogView(getContext());

            dialog.setTitleText(GUISetup.getTextForPermResid(need));
            dialog.setInfoText(GUISetup.getInfoForPermResid(need));

            if (! GUISetup.needHasPermissions(need))
            {
                dialog.setPositiveButton(R.string.basic_ok, null);
                dialog.positiveButton.requestFocus();
            }
            else
            {
                if (GUISetup.haveAllPermissionsForNeed(getContext(), need))
                {
                    dialog.setPositiveButton(R.string.basic_deactiviate, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUISetup.requestPermissionForNeed((Activity) getContext(), need, 4711);
                        }
                    });

                    dialog.setNegativeButton(R.string.basic_cancel);

                    dialog.negativeButton.requestFocus();
                }
                else
                {
                    dialog.setPositiveButton(R.string.basic_activiate, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUISetup.requestPermissionForNeed((Activity) getContext(), need, 4711);
                        }
                    });

                    dialog.setNegativeButton(R.string.basic_postpone);

                    dialog.positiveButton.requestFocus();
                }
            }

            GUI.instance.desktopActivity.topframe.addView(dialog);
        }
    };
}
