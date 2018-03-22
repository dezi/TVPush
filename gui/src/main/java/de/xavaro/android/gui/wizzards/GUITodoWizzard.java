package de.xavaro.android.gui.wizzards;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import de.xavaro.android.gui.base.GUIPluginTitleList;
import de.xavaro.android.gui.simple.Simple;
import de.xavaro.android.gui.R;
import de.xavaro.android.gui.views.GUIListView;

public class GUITodoWizzard extends GUIPluginTitleList
{
    private final static String LOGTAG = GUITodoWizzard.class.getSimpleName();

    public GUITodoWizzard(Context context)
    {
        super(context);

        setIsWizzard(true, false, false);

        setTitleIcon(R.drawable.todo_list_512);
        setTitleText("Todo-Liste");
    }

    @Override
    public void onCollectEntries(GUIListView listView, boolean todo)
    {
        GUIPermissionWizzard.collectEntries(listView, true);
        GUILocationsWizzard.collectEntries(listView, true);
    }
}
