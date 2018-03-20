package de.xavaro.android.gui.wizzards;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.gui.base.GUIDefs;
import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.simple.Json;
import de.xavaro.android.gui.views.GUIListEntry;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.views.GUISeparatorView;
import de.xavaro.android.gui.R;

public class GUITodoWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUITodoWizzard.class.getSimpleName();

    public GUITodoWizzard(Context context)
    {
        super(context);

        setTitleIcon(R.drawable.todo_list_512);
        setTitleText("Todo-Liste");

        Simple.getHandler().post(makeTodoList);
    }

    private final Runnable makeTodoList = new Runnable()
    {
        @Override
        public void run()
        {
            listView.removeAllViews();

            checkServices();
            checkPermissions();

            Simple.getHandler().postDelayed(makeTodoList, 10 * 1000);
        }
    };

    private void checkServices()
    {
        JSONObject services = GUISetup.getRequiredServices();

        Iterator<String> keys = services.keys();

        while (keys.hasNext())
        {
            String service = keys.next();
            boolean enabled = Json.getBoolean(services, service);

            if (listView.getChildCount() > 0)
            {
                listView.addView(new GUISeparatorView(getContext()));
            }

            GUIListEntry entry = new GUIListEntry(getContext());
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

    private void checkPermissions()
    {
        JSONObject areas = GUISetup.getRequiredPermissions();

        Iterator<String> keys = areas.keys();

        while (keys.hasNext())
        {
            String area = keys.next();
            JSONArray perms = Json.getArray(areas, area);
            if (perms == null) continue;

            if (listView.getChildCount() > 0)
            {
                listView.addView(new GUISeparatorView(getContext()));
            }

            GUIListEntry entry = new GUIListEntry(getContext());
            entry.setOnClickListener(onAreaPermissionClickListener);
            entry.setTag(area);

            boolean enabled = GUISetup.checkPermissions(getContext(), area);
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

    private OnClickListener onServiceStartClickListener = new OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            String service = (String) view.getTag();
            GUISetup.startIntentForService(view.getContext(), service);
        }
    };

    private OnClickListener onAreaPermissionClickListener = new OnClickListener()
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

                Simple.getHandler().removeCallbacks(makeTodoList);
                Simple.getHandler().post(makeTodoList);
            }
            else
            {
                Log.d(LOGTAG, "onRequestPermissionsResult: boo=" + permissions[ 0 ]);
            }
        }
    }
}
