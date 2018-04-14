package de.xavaro.android.gui.wizzards;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import de.xavaro.android.gui.base.GUIIcons;
import pub.android.interfaces.all.SubSystemHandler;

import de.xavaro.android.gui.plugin.GUIPluginTitleList;
import de.xavaro.android.gui.views.GUIDialogView;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.R;

public class GUISettingsWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUISettingsWizzard.class.getSimpleName();

    public GUISettingsWizzard(Context context)
    {
        super(context);

        setWizzard(true, true, 1, Gravity.END);

        setTitleIcon(R.drawable.wizzard_settings_200);
        setNameText("Subsystem Settings");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    private void collectEntries(GUIListView listView, boolean todo)
    {
        String subsystem = objectTag;
        if (subsystem == null) return;

        JSONObject infos = GUI.instance.onGetSubsystemSettings(subsystem);
        if (infos == null) return;

        String name = Json.getString(infos, "name");
        if (name == null) return;

        String type = Json.getString(infos, "type");
        if (type == null) type = SubSystemHandler.SUBSYSTEM_TYPE_SERVICE;

        int mode = Json.getInt(infos, "mode");
        String icon = Json.getString(infos, "icon");
        String need = Json.getString(infos, "need");

        int state = GUISetup.getSubsystemState(subsystem);
        int runstate = GUISetup.getSubsystemRunState(subsystem);
        boolean enabled = (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);

        String info = GUISetup.getTextForSubsystemEnabled(name, state, mode);

        if (enabled && type.equals(SubSystemHandler.SUBSYSTEM_TYPE_SERVICE))
        {
            info += " - " + Simple.getTrans(GUISetup.getTextForSubsystemRunstateResid(runstate));
        }

        int color = ((runstate == SubSystemHandler.SUBSYSTEM_RUN_STARTED)
                        || (mode == SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE))
                ? GUIDefs.TEXT_COLOR_INFOS
                : enabled
                ? GUIDefs.TEXT_COLOR_ALERTS
                : GUIDefs.TEXT_COLOR_SPECIAL;

        GUIListEntry entry = listView.findGUIListEntryOrCreate(subsystem);
        entry.setOnClickListener(onSubsystemClickListener);
        entry.setTag(infos);

        entry.iconView.setImageResource(icon);
        entry.headerViev.setText(name);
        entry.infoView.setText(info);
        entry.infoView.setTextColor(color);

        collectSubsystemsNeeds(listView, subsystem, need, enabled, todo);

        collectSubsystemsSettings(listView, subsystem, todo);
    }

    private void collectSubsystemsSettings(GUIListView listView, String subsystem, boolean todo)
    {
        JSONObject infos = GUI.instance.onGetSubsystemSettings(subsystem);
        if (infos == null) return;

        JSONArray settings = Json.getArray(infos, "settings");
        if (settings == null) return;

        for (int inx = 0; inx < settings.length(); inx++)
        {
            JSONObject setting = Json.getObject(settings, inx);
            if (setting == null) continue;

            String tag = Json.getString(setting, "tag");
            String uuid = Json.getString(setting, "uuid");
            String type = Json.getString(setting, "type");
            String name = Json.getString(setting, "name");

            if (((tag == null) && (uuid == null)) || (type == null) || (name == null)) continue;

            String icon = Json.getString(setting, "icon");

            String subtag = subsystem + "." + ((tag != null) ? tag : uuid);

            int mode = Json.getInt(setting, "mode");
            int state = GUISetup.getSubsystemState(subtag);
            int runstate = GUISetup.getSubsystemRunState(subtag);

            boolean enabled = (state == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);
            boolean service = type.equals(SubSystemHandler.SUBSYSTEM_TYPE_SERVICE);

            String info = GUISetup.getTextForSubsystemEnabled(name, state, mode);

            if (enabled && service)
            {
                info += " - " + Simple.getTrans(GUISetup.getTextForSubsystemRunstateResid(runstate));
            }

            int color = (! enabled)
                    ? GUIDefs.TEXT_COLOR_SPECIAL
                    : ((runstate == SubSystemHandler.SUBSYSTEM_RUN_STARTED) || ! service)
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS;

            GUIListEntry entry = listView.findGUIListEntryOrCreate(subtag);
            entry.setOnClickListener(onSettingClickListener);
            entry.setTag(setting);

            if (icon != null)
            {
                entry.iconView.setImageResource(icon);
            }
            else
            {
                if (uuid != null)
                {
                    entry.iconView.setImageResource(GUIIcons.getImageResid(uuid));
                }
            }

            entry.headerViev.setText(name);
            entry.infoView.setText(info);

            entry.infoView.setTextColor(color);

            String need = Json.getString(setting, "need");

            collectSubsystemsNeeds(listView, subtag, need, enabled, todo);
        }
    }

    private void collectSubsystemsNeeds(GUIListView listView, String subtag, String needString, boolean needEnabled, boolean todo)
    {
        if (needString == null) return;

        String[] needs = needString.split("\\+");

        for (String need : needs)
        {
            if (GUISetup.needHasAuth(need)
                    || GUISetup.needHasInfos(need)
                    || GUISetup.needHasConfig(need)
                    || GUISetup.needHasService(need))
            {
                boolean enabled = GUISetup.haveNeed(need);

                int icon = GUISetup.getIconForNeedResid(need);
                int text = GUISetup.getTextForNeedResid(need);

                GUIListEntry entry = listView.findGUIListEntryOrCreate(subtag + "." + need);
                entry.setOnClickListener(onNeedClickListener);
                entry.setTag(need);

                entry.setLevel(1);
                entry.iconView.setImageResource(icon);
                entry.headerViev.setText(text);

                entry.infoView.setText(GUISetup.getTextForNeedStatusResid(need, enabled));

                entry.infoView.setTextColor(enabled
                        ? GUIDefs.TEXT_COLOR_INFOS
                        : needEnabled
                        ? GUIDefs.TEXT_COLOR_ALERTS
                        : GUIDefs.TEXT_COLOR_SPECIAL);
            }

            collectNeedPermissions(listView, need, needEnabled, todo);
        }
    }

    @SuppressWarnings("StringConcatenationInLoop")
    private void collectNeedPermissions(GUIListView listView, String need, boolean needEnabled, boolean todo)
    {
        JSONArray perms = GUISetup.getPermissionsForNeed(need);
        if ((perms == null) || (perms.length() == 0)) return;

        boolean enabled = GUISetup.haveAllPermissionsForNeed(getContext(), need);

        int icon = GUISetup.getIconForPermResid(need);
        int text = GUISetup.getTextForPermResid(need);

        String infos = "";

        for (int inx = 0; inx < perms.length(); inx++)
        {
            String perm = Json.getString(perms, inx);
            if (perm == null) continue;

            if (infos.length() > 0) infos += ", ";

            infos += Simple.getTrans(GUISetup.getTextForManifestPermResid(perm));
        }

        GUIListEntry entry = listView.findGUIListEntryOrCreate(infos);
        entry.setOnClickListener(onPermissionsClickListener);
        entry.setTag(need);

        entry.setLevel(1);
        entry.iconView.setImageResource(icon);
        entry.headerViev.setText(text);
        entry.infoView.setText(infos);

        entry.infoView.setTextColor(enabled
                ? GUIDefs.TEXT_COLOR_INFOS
                : needEnabled
                ? GUIDefs.TEXT_COLOR_ALERTS
                : GUIDefs.TEXT_COLOR_SPECIAL);
    }

    private final OnClickListener onSubsystemClickListener = new OnClickListener()
    {
        @Override
        public void onClick(final View entry)
        {
            JSONObject infos = (JSONObject) entry.getTag();
            final String subsystem = Json.getString(infos, "drv");
            if (subsystem == null) return;

            GUIDialogView dialog = new GUIDialogView(entry.getContext());

            dialog.setTitleText(Json.getString(infos, "name"));
            dialog.setInfoText(Json.getString(infos, "info"));

            int mode = Json.getInt(infos, "mode");

            if ((mode == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
                    || (mode == SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE))
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
                            GUI.instance.onStopSubsystemRequest(subsystem);
                            GUIPluginTitleList.updateContentinParentPlugin(entry);
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

                    dialog.setNegativeButton(R.string.basic_postpone);

                    dialog.positiveButton.requestFocus();
                }
            }

            GUI.instance.desktopActivity.topframe.addView(dialog);
        }
    };

    private final OnClickListener onSettingClickListener = new OnClickListener()
    {
        @Override
        public void onClick(final View entry)
        {
            String subsystem = objectTag;
            if (subsystem == null) return;

            JSONObject infos = (JSONObject) entry.getTag();

            String tag = Json.getString(infos, "tag");
            String uuid = Json.getString(infos, "uuid");
            if ((tag == null) && (uuid == null)) return;

            final String subtag = subsystem + "." + ((tag != null) ? tag : uuid);

            GUIDialogView dialog = new GUIDialogView(entry.getContext());

            dialog.setTitleText(Json.getString(infos, "name"));
            dialog.setInfoText(Json.getString(infos, "info"));

            int mode = Json.getInt(infos, "mode");

            if ((mode == SubSystemHandler.SUBSYSTEM_MODE_MANDATORY)
                || (mode == SubSystemHandler.SUBSYSTEM_MODE_IMPOSSIBLE))
            {
                dialog.setPositiveButton(R.string.basic_ok);
                dialog.positiveButton.requestFocus();
            }
            else
            {
                if (GUISetup.getSubsystemState(subtag) == SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED)
                {
                    dialog.setPositiveButton(R.string.basic_deactiviate, new OnClickListener()
                    {
                        @Override
                        public void onClick(View view)
                        {
                            GUI.instance.subSystems.setSubsystemState(subtag, SubSystemHandler.SUBSYSTEM_STATE_DEACTIVATED);
                            GUI.instance.onStopSubsystemRequest(subtag);
                            GUIPluginTitleList.updateContentinParentPlugin(entry);
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
                            GUI.instance.subSystems.setSubsystemState(subtag, SubSystemHandler.SUBSYSTEM_STATE_ACTIVATED);
                            GUI.instance.onStartSubsystemRequest(subtag);
                            GUIPluginTitleList.updateContentinParentPlugin(entry);
                        }
                    });

                    dialog.setNegativeButton(R.string.basic_postpone);

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

            if (! (GUISetup.needHasService(need) || GUISetup.needHasConfig(need)))
            {
                dialog.setPositiveButton(R.string.basic_ok, null);
                dialog.positiveButton.requestFocus();
            }
            else
            {
                if (GUISetup.needHasConfig(need))
                {
                    dialog.setPositiveButton(R.string.basic_configure, new OnClickListener()
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
            }

            GUI.instance.desktopActivity.topframe.addView(dialog);
        }
    };

    private final OnClickListener onPermissionsClickListener = new OnClickListener()
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
