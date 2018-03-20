package de.xavaro.android.gui.wizzards;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

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

            checkPermissions();

            Simple.getHandler().postDelayed(makeTodoList, 600 * 1000);
        }
    };

    private void checkPermissions()
    {
        JSONObject areas = GUISetup.getRequiredPermissions();

        Iterator<String> keys = areas.keys();

        while (keys.hasNext())
        {
            String key = keys.next();
            JSONArray perms = Json.getArray(areas, key);
            if (perms == null) continue;

            if (listView.getChildCount() > 0)
            {
                listView.addView(new GUISeparatorView(getContext()));
            }

            GUIListEntry entry = new GUIListEntry(getContext());

            String infos = "";

            for (int inx = 0; inx < perms.length(); inx++)
            {
                String perm = Json.getString(perms, inx);

                if (infos.length() > 0) infos += ", ";

                infos += Simple.getTrans(GUISetup.getTextForPermissionResid(perm));
            }

            String head = Simple.getTrans(GUISetup.getTextPermissionResid())
                    + ": "
                    + Simple.getTrans(GUISetup.getTextForAreaResid(key));

            entry.iconView.setImageResource(GUISetup.getIconForAreaResid(key));
            entry.headerViev.setText(head);
            entry.infoView.setText(infos);

            listView.addView(entry);
        }
    }
}
