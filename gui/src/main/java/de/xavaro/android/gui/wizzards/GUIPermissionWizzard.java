package de.xavaro.android.gui.wizzards;

import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.gui.R;
import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.views.GUISeparatorView;

public class GUIPermissionWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUIPermissionWizzard.class.getSimpleName();

    public GUIPermissionWizzard(Context context)
    {
        super(context);

        setTitleIcon(R.drawable.todo_list_512);
        setTitleText("Services and Permissions");

        Simple.getHandler().post(makeEntryList);
    }

    public static void collectEntriesTodo(GUILinearLayout listView)
    {
        collectEntries(listView, true);
    }

    private static void collectEntries(GUILinearLayout listView, boolean todo)
    {
        collectServices(listView, todo);
        collectPermissions(listView, todo);
    }

    private final Runnable makeEntryList = new Runnable()
    {
        @Override
        public void run()
        {
            listView.removeAllViews();

            collectEntries(listView, false);

            Simple.getHandler().postDelayed(makeEntryList, 10 * 1000);
        }
    };

    private static void collectServices(GUILinearLayout listView, boolean todo)
    {
        JSONObject services = GUISetup.getRequiredServices();

        Iterator<String> keys = services.keys();

        while (keys.hasNext())
        {
            String service = keys.next();
            boolean enabled = Json.getBoolean(services, service);
            if (todo && enabled) continue;

            if (listView.getChildCount() > 0)
            {
                listView.addView(new GUISeparatorView(listView.getContext()));
            }

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
        JSONObject areas = GUISetup.getRequiredPermissions();

        Iterator<String> keys = areas.keys();

        while (keys.hasNext())
        {
            String area = keys.next();
            boolean enabled = GUISetup.checkPermissions(listView.getContext(), area);
            if (todo && enabled) continue;

            JSONArray perms = Json.getArray(areas, area);
            if (perms == null) continue;

            if (listView.getChildCount() > 0)
            {
                listView.addView(new GUISeparatorView(listView.getContext()));
            }

            GUIListEntry entry = new GUIListEntry(listView.getContext());
            entry.setOnClickListener(onAreaPermissionClickListener);
            entry.setTag(area);

            String infos = "";

            for (int inx = 0; inx < perms.length(); inx++)
            {
                String perm = Json.getString(perms, inx);

                if (infos.length() > 0) infos += ", ";

                infos += Simple.getTrans(GUISetup.getTextForPermissionResid(perm));
            }

            String head = Simple.getTrans(GUISetup.getTextPermissionResid())
                    + ": "
                    + Simple.getTrans(GUISetup.getTextForAreaResid(area));

            entry.iconView.setImageResource(GUISetup.getIconForAreaResid(area));
            entry.headerViev.setText(head);
            entry.infoView.setText(infos);

            entry.infoView.setTextColor(enabled
                    ? GUIDefs.TEXT_COLOR_INFOS
                    : GUIDefs.TEXT_COLOR_ALERTS);

            listView.addView(entry);
        }
    }

    private static OnClickListener onServiceStartClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String service = (String) view.getTag();
            GUISetup.startIntentForService(view.getContext(), service);
        }
    };

    private static OnClickListener onAreaPermissionClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String area = (String) view.getTag();
            GUISetup.requestPermission((Activity) view.getContext(), area, 4711);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults)
    {
        if ((requestCode == 4711) && (permissions.length > 0) && (grantResults.length > 0))
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.d(LOGTAG, "onRequestPermissionsResult: yep=" + permissions[ 0 ]);

                Simple.getHandler().removeCallbacks(makeEntryList);
                Simple.getHandler().post(makeEntryList);
            }
            else
            {
                Log.d(LOGTAG, "onRequestPermissionsResult: boo=" + permissions[ 0 ]);
            }
        }
    }
}
