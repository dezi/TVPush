package de.xavaro.android.gui.wizzards;

import android.content.Context;

import org.json.JSONObject;

import java.util.Iterator;

import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.base.GUISetup;
import de.xavaro.android.gui.views.GUILinearLayout;
import de.xavaro.android.gui.views.GUIScrollView;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.R;

public class GUITodoWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUITodoWizzard.class.getSimpleName();

    private GUIScrollView scrollView;
    private GUILinearLayout listView;

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
            checkPermissions();

            Simple.getHandler().postDelayed(makeTodoList, 600 * 1000);
        }
    };

    private void checkPermissions()
    {
        JSONObject perms = GUISetup.getRequiredPermissions();

        Iterator<String> keys = perms.keys();

        while (keys.hasNext())
        {
            String key = keys.next();

        }
    }
}
