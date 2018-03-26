package de.xavaro.android.gui.wizzards;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUI;
import de.xavaro.android.gui.base.GUISubSystems;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.views.GUIDialogView;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUIListView;

public class GUISetupWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUISetupWizzard.class.getSimpleName();

    public GUISetupWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false);

        setTitleIcon(R.drawable.permissions_240);
        setTitleText("System Setup");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        collectEntries(listView, todo);
    }

    public static void collectEntries(GUILinearLayout listView, boolean todo)
    {
        collectServices(listView, todo);
        collectPermissions(listView, todo);
        collectFeatures(listView, todo);
        collectSubsystems(listView, todo);
    }

    private static void collectServices(GUILinearLayout listView, boolean todo)
    {
        JSONObject services = GUISetup.getRequiredServices();

        Iterator<String> keys = services.keys();

        while (keys.hasNext())
        {
            String service = keys.next();
            boolean enabled = Json.getBoolean(services, service);
            if (todo && enabled) continue;

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onServiceStartClickListener);
            entry.setTag(service);

            String head = Simple.getTrans(GUISetup.getTextServiceResid())
                    + ": "
                    + Simple.getTrans(GUISetup.getTextForServiceResid(service));

            entry.iconView.setImageResource(GUISetup.getIconForServiceResid(service));
            entry.headerViev.setText(head);

            entry.infoView.setText(GUISetup.getTextForServiceEnabledResid(service, enabled));

            entry.infoView.setTextColor(enabled
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);

            listView.addView(entry);
        }
    }

    private static void collectPermissions(GUILinearLayout listView, boolean todo)
    {
        JSONObject services = GUISetup.getRequiredPermissions();

        Iterator<String> keys = services.keys();

        while (keys.hasNext())
        {
            String service = keys.next();

            boolean enabled = GUISetup.haveAllPermissions(listView.getContext(), service);
            if (todo && enabled) continue;

            JSONArray perms = Json.getArray(services, service);
            if (perms == null) continue;

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onPermissionRequestClickListener);
            entry.setTag(service);

            String infos = "";

            for (int inx = 0; inx < perms.length(); inx++)
            {
                String perm = Json.getString(perms, inx);

                if (infos.length() > 0) infos += ", ";

                infos += Simple.getTrans(GUISetup.getTextForPermissionResid(perm));
            }

            String head = Simple.getTrans(GUISetup.getTextPermissionResid())
                    + ": "
                    + Simple.getTrans(GUISetup.getTextForServiceResid(service));

            entry.iconView.setImageResource(GUISetup.getIconForServiceResid(service));
            entry.headerViev.setText(head);
            entry.infoView.setText(infos);

            entry.infoView.setTextColor(enabled
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);

            listView.addView(entry);
        }
    }

    private static void collectFeatures(GUILinearLayout listView, boolean todo)
    {
        JSONObject features = GUISetup.getRequiredFeatures();

        Iterator<String> keys = features.keys();

        while (keys.hasNext())
        {
            String feature = keys.next();
            boolean enabled = Json.getBoolean(features, feature);
            if (todo && enabled) continue;

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onFeatureStartClickListener);
            entry.setTag(feature);

            String head = Simple.getTrans(GUISetup.getTextFeatureResid())
                    + ": "
                    + Simple.getTrans(GUISetup.getTextForFeatureResid(feature));

            entry.iconView.setImageResource(GUISetup.getIconForFeatureResid(feature));
            entry.headerViev.setText(head);

            entry.infoView.setText(GUISetup.getTextForFeatureEnabledResid(feature, enabled));

            entry.infoView.setTextColor(enabled
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);

            listView.addView(entry);
        }
    }

    private static void collectSubsystems(GUILinearLayout listView, boolean todo)
    {
        JSONArray subsystems = GUISetup.getAvailableSubsystems();

        for (int inx = 0; inx < subsystems.length(); inx++)
        {
            JSONObject subsystem = Json.getObject(subsystems, inx);
            if (subsystem == null) continue;

            String drv = Json.getString(subsystem, "drv");
            String name = Json.getString(subsystem, "name");

            int state = GUISetup.getSubsystemState(drv);

            if (todo && (state != GUISubSystems.SUBSYSTEM_STATE_INACTIVE)) continue;

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onSubsystemStartClickListener);
            entry.setTag(subsystem);

            String head = Simple.getTrans(GUISetup.getTextSubsystemResid())
                    + ": "
                    + name;

            entry.iconView.setImageResource(GUISetup.getIconForSubsystemResid(drv));
            entry.headerViev.setText(head);

            entry.infoView.setText(GUISetup.getTextForSubsystemEnabled(name, state));

            int color = (state == GUISubSystems.SUBSYSTEM_STATE_INACTIVE)
                    ? GUIDefs.TEXT_COLOR_ALERTS
                    : (state == GUISubSystems.SUBSYSTEM_STATE_DISABLED)
                    ? GUIDefs.TEXT_COLOR_SPECIAL
                    : GUIDefs.TEXT_COLOR_INFOS;

            entry.infoView.setTextColor(color);

            listView.addView(entry);
        }
    }

    private static final OnClickListener onServiceStartClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String service = (String) view.getTag();
            GUISetup.startIntentForService(view.getContext(), service);
        }
    };

    private static final OnClickListener onPermissionRequestClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String service = (String) view.getTag();
            GUISetup.requestPermission((Activity) view.getContext(), service, 4711);
        }
    };

    private static final OnClickListener onFeatureStartClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
        }
    };

    private static final OnClickListener onSubsystemStartClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            JSONObject subsystem = (JSONObject) view.getTag();
            String drv = Json.getString(subsystem, "drv");

            GUIDialogView dialog = new GUIDialogView(view.getContext());

            dialog.setTitleText(GUISetup.getTitleForSubsystemResid(drv));
            dialog.setInfoText(GUISetup.getInfoForSubsystemResid(drv));

            if (GUISetup.getSubsystemState(drv) == GUISubSystems.SUBSYSTEM_STATE_ACTIVATED)
            {
                dialog.setPositiveButton(R.string.basic_deactiviate, new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                    }
                });

                dialog.setNegativeButton(R.string.basic_cancel, new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                    }
                });
            }
            else
            {
                dialog.setPositiveButton(R.string.basic_activiate, new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                    }
                });

                dialog.setNegativeButton(R.string.basic_postpone, new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {

                    }
                });
            }


            GUI.instance.desktopActivity.topframe.addView(dialog);
        }
    };
}
